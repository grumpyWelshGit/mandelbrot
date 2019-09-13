package uk.org.landeg.mandel;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MandelbrotDriver {
  @Bean
  CommandLineRunner runIt() {
    return args -> {
      
      final MandelbrotMap map = new MandelbrotMap();
      final Rectangle2D bounds = new Rectangle(-2, -2, 4, 4);
      map.initMap(bounds, new Point(200, 200));
      final BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
      int[] buffer = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
      for (int x = 0 ; x < 200 ; x++) {
        for (int y = 0 ; y < 200 ; y++) {
          map.iterateUntil(x, y, 200);
          final int it = map.getIterations(x, y);
          int col = Color.black.getRGB();
          if (it == 1) {
            col = Color.blue.getRGB();
          }
          if (it > 1 && it < 200) {
            col = Color.red.getRGB();
          }
          buffer[y * 200 + x] =  col;
        }
      }
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
