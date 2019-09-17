package uk.org.landeg.mandel;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import uk.org.landeg.mandel.Mathlib.PrimeContext;
import uk.org.landeg.mandel.ui.FractalViewerFrame;
import uk.org.landeg.mandel.worker.MandelbrotWorker;

@Service
public class MandelbrotService {
  @Value("${worker.threadcount}")
  private Integer threadCount;

  private int mapSize = 1024;

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private FractalViewerFrame viewer;

  @Autowired
  private ColorMapper colorMapper;

  @Autowired
  private MandelbrotMap map;

  @Autowired
  private BlockingQueue<Rectangle> queue;

  @Autowired
  private MandelbrotContext context;

  @Autowired
  private PrimeContextHolder primeContextHolder;
  
  private PrimeContext primeContext;

  static Map<Integer, Integer> hsvColorMap = new HashMap<>();

  final List<Thread> workers = new ArrayList<>();
  
  Logger log = LoggerFactory.getLogger(this.getClass());

  final Rectangle screenBounds = new Rectangle(0, 0, mapSize, mapSize);
  final BufferedImage image = new BufferedImage(mapSize, mapSize, BufferedImage.TYPE_INT_RGB);
  int[] buffer = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();

  @PostConstruct
  public void initWorkers() {
    for (int threadId = 0 ; threadId < threadCount ; threadId++) {
      workers.add(new Thread(applicationContext.getBean(MandelbrotWorker.class)));
    }
    workers.stream().forEach(worker -> worker.start());
  }

  @PostConstruct
  public void initMap() {
    primeContextHolder.setContext(Mathlib.getPrimeFactors(mapSize));
  }

  public void initialise() throws InterruptedException {
    int maxIterations = 1000;

    context.setMap(map);
    context.setMaxIterations(maxIterations);
    context.setPrimeContext(primeContext);
    render(0, 0, 4);
  }

  public void increaseDepth() {
    context.setMaxIterations(context.getMaxIterations() + 100);
    queue.offer(screenBounds);
    waitForRender();
    mapToImage();
    viewer.setImage(image);
  }

  public void decreaseDepth() {
    context.setMaxIterations(context.getMaxIterations() - 100);
    queue.offer(screenBounds);
    waitForRender();
    mapToImage();
    viewer.setImage(image);
  }

  public void renderDefault() {
    this.render(0, 0, 4);
  }

  public void repaint() {
    map.initMap(context.getBounds(), new Point(mapSize, mapSize));
    queue.offer(screenBounds);
    waitForRender();
    mapToImage();
    viewer.setImage(image);
  }

  public void render(double xCentre, double yCentre, double size) {
    log.info("starting render {} {} {}", xCentre, yCentre, size);
    final BoundingBox bounds = new BoundingBox(xCentre - size/2.0, yCentre - size/2.0, size, size);
    map.initMap(bounds, new Point(mapSize, mapSize));
    
    context.setBounds(bounds);
    
    queue.offer(screenBounds);

    final long start = System.currentTimeMillis();

    waitForRender();

    mapToImage();

    long end = System.currentTimeMillis();
    System.out.println("image rendered in " + (end - start) + "ms");
    this.viewer.setImage(image);
  }

  @PreDestroy
  public void shutdownWorkers() {
    workers.stream().forEach(worker -> worker.interrupt());
    workers.clear();
  }

  private void waitForRender() {
    log.info("Waiting for render to complete");
    while (queue.contains(screenBounds));
    do {
      synchronized (queue) {
        try {
          queue.wait();
        } catch (InterruptedException e) {
          e.printStackTrace();
          throw new RuntimeException(e);
        }
      }
    } while (queue.size() > 0);
    log.info("Render complete");
  }

  private void mapToImage() {
    log.info("render complete, filling image");
    for (int x = 0 ; x < mapSize ; x++) {
      for (int y = 0 ; y < mapSize ; y++) {
        final int it = map.iterations[x][y];
        int col = hsvColorMap.computeIfAbsent(it, i -> colorMapper.apply(i, context.getMaxIterations()));
        buffer[y * mapSize + x] =  col;
      }
    }
  }

  public int getMapSize() {
    return mapSize;
  }
}
