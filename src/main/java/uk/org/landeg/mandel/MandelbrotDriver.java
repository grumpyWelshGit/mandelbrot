package uk.org.landeg.mandel;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import uk.org.landeg.mandel.Mathlib.PrimeContext;
import uk.org.landeg.mandel.ui.FractalViewerFrame;

@Configuration
public class MandelbrotDriver {
  static int maxIterations = 1000;
  static PrimeContext primeContext;
  volatile int buffer[];
  static Object threadLock = new Object();
  static CountDownLatch latch;
  Logger log = LoggerFactory.getLogger(this.getClass());
  static Map<Integer, Integer> hsvColorMap = new HashMap<>();

  @Autowired
  private FractalViewerFrame viewer;

  @Autowired
  private ColorMapper colorMapper;

  @Autowired
  private MandelbrotMap map;

  @Bean
  CommandLineRunner runIt() {
    return args -> {
      int mapSize = 1000;
      primeContext = Mathlib.getPrimeFactors(mapSize);
      final MandelbrotProcessor processor = new MandelbrotProcessor();

      final BoundingBox bounds = new BoundingBox(-2.0, -2.0, 4, 4);
      final Rectangle screenBounds = new Rectangle(0,0,mapSize,mapSize);
      BlockingQueue<Rectangle> subCells = new ArrayBlockingQueue<>(mapSize * mapSize / 10); 

      final int threadCount = 3;
      final List<Thread> workers = new ArrayList<>();
      latch = new CountDownLatch(threadCount);
      for (int threadId = 0 ; threadId < threadCount ; threadId++) {
        workers.add(new Thread(new WorkerThread(subCells, processor, map)));
      }
      workers.stream().forEach(worker -> worker.start());

      
      map.initMap(bounds, new Point(mapSize, mapSize));
      final BufferedImage image = new BufferedImage(mapSize, mapSize, BufferedImage.TYPE_INT_RGB);
      buffer = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();

      subCells.offer(screenBounds);

      final long start = System.currentTimeMillis();

      while (subCells.contains(screenBounds));
      do {
        synchronized (threadLock) {
          threadLock.wait();
        }
      } while (subCells.size() > 0);
      workers.stream().forEach(worker -> worker.interrupt());
      workers.clear();

      for (int x = 0 ; x < mapSize ; x++) {
        for (int y = 0 ; y < mapSize ; y++) {
          final int it = map.iterations[x][y];
          int col = hsvColorMap.computeIfAbsent(it, i -> colorMapper.apply(i, maxIterations));
          buffer[y * mapSize + x] =  col;
        }
      }
      long end = System.currentTimeMillis();
      System.out.println("image rendered in " + (end - start) + "ms");
      this.viewer.setImage(image);
      try {
//        final OutputStream os = new FileOutputStream(new File(String.format("mandelbrot_%d.png", System.currentTimeMillis())));
        final OutputStream os = new FileOutputStream(new File(String.format("mandelbrot.png", System.currentTimeMillis())));
        ImageIO.write(image, "png", os);
        os.flush();
        os.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    };
  }

  static class WorkerThread implements Runnable {
    private final BlockingQueue<Rectangle> queue;
    private final MandelbrotProcessor processor;
    private final MandelbrotMap map;
    Logger log = LoggerFactory.getLogger(this.getClass());

    public WorkerThread(
        BlockingQueue<Rectangle> queue, 
        MandelbrotProcessor processor, 
        MandelbrotMap map) {
      super();
      this.queue = queue;
      this.processor = processor;
      this.map = map;
    }

    @Override
    public void run() {
      Rectangle cell;
      boolean notify = false;
      try {
        while ((cell = queue.take()) != null) {
          notify = true;
//          log.info("{} processing {}", Thread.currentThread().getName(), cell);
          final int detail = processor.solvePerimiter(map, cell, maxIterations);
          if (detail > 0) {
            final List<Rectangle> newCells = Mathlib.split(cell, primeContext);
            if (!newCells.isEmpty()) {
              for (Rectangle c : newCells) {
                queue.put(c);
                notify = false;
              }
            } 
          } else {
            // fill cell
            if (cell.width > 2) {
              for (int dx = 0 ; dx < cell.getWidth() ; dx++) {
                for (int dy = 0 ; dy < cell.getHeight() ; dy++) {
                  map.iterations[cell.x + dx][cell.y + dy] = map.iterations[cell.x][cell.y];
                }
              }
            }
          }
          if (notify && queue.peek() == null) {
              synchronized (threadLock) {
                log.debug("notifying");
                threadLock.notifyAll();
            }
          }
        }
      } catch (InterruptedException e) {
        log.info("worker thread exiting");
      }
    }
  }
}
