package uk.org.landeg.mandel;

import java.awt.Color;
import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ColorMapper implements BiFunction<Integer, Integer, Integer>{
  Logger log = LoggerFactory.getLogger(this.getClass());

  @Override
  public Integer apply(Integer ii, Integer maxIterations) {
    int i = ii == null? 0 : ii.intValue();
    int newcol = Color.white.getRGB();
    if (i > 0) {
      if (i == 1) {
        newcol = Color.blue.getRGB();
      }
      else if (i == maxIterations.intValue()) {
        newcol = Color.BLACK.getRGB();
      }
      else {
        newcol = Color.HSBtoRGB(i/200.0f, 1f, 1.0f);
      }
    }
    log.debug("generating color for {} iterations {}", i, newcol);
    return newcol;
  }

}
