package uk.org.landeg.mandel;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.imageio.ImageIO;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import uk.org.landeg.mandel.Mathlib.PrimeContext;

@Configuration
public class MandelbrotDriver {
  @Bean
  CommandLineRunner runIt() {
    return args -> {
      final long start = System.currentTimeMillis();
      int mapSize = 1200;
      int maxIterations = 200;
      final PrimeContext primeContext = Mathlib.getPrimeFactors(mapSize);
      final MandelbrotMap map = new MandelbrotMap();
      final MandelbrotProcessor processor = new MandelbrotProcessor();

      final BoundingBox bounds = new BoundingBox(-2.0, -2.0, 4, 4);
      final Rectangle screenBounds = new Rectangle(0,0,mapSize,mapSize);
      Queue<Rectangle> subCells = new ConcurrentLinkedQueue<>(); 
      
      subCells.add(screenBounds);
      
      map.initMap(bounds, new Point(mapSize, mapSize));
      final BufferedImage image = new BufferedImage(mapSize, mapSize, BufferedImage.TYPE_INT_RGB);
      int[] buffer = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
      int cellsCreated = 0;
      Rectangle cell = null;
      while ((cell = subCells.poll()) != null) {
          final int detail = processor.solvePerimiter(map, cell, maxIterations);
          if (cell.width > 2 && detail > 0) {
            final List<Rectangle> newCells = Mathlib.split(cell, primeContext); 
            subCells.addAll(newCells);
            cellsCreated+=newCells.size();
          } else {
            // fill cell
//            for (int dx = 0 ; dx < cell.getWidth() ; dx++) {
//              for (int dy = 0 ; dy < cell.getHeight() ; dy++) {
//                map.iterations[cell.x + dx][cell.y + dy] = map.iterations[cell.x][cell.y];
//              }
//            }
          }
      }

      System.out.println("total cells " + cellsCreated);
      for (int x = 0 ; x < mapSize ; x++) {
        for (int y = 0 ; y < mapSize ; y++) {
          final int it = map.iterations[x][y];
          int col = Color.white.getRGB();
          if (it > 0) {
            if (it == 1) {
              col = Color.blue.getRGB();
            }
            if (it > 1 && it < maxIterations) {
              col = Color.red.getRGB();
            }
            if (it == maxIterations) {
              col = Color.BLACK.getRGB();
            }
          }
          buffer[y * mapSize + x] =  col;
        }
      }
      long end = System.currentTimeMillis();
      System.out.println("image rendered in " + (end - start) + "ms");
      try {
        final OutputStream os = new FileOutputStream(new File(String.format("mandelbrot_%d.png", System.currentTimeMillis())));
        ImageIO.write(image, "png", os);
        os.flush();
        os.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    };
  }
}
