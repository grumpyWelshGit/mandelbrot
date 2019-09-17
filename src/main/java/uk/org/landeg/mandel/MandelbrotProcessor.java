package uk.org.landeg.mandel;

import java.awt.Rectangle;

import org.springframework.stereotype.Component;

@Component
public class MandelbrotProcessor {
  public int iterateUntil(MandelbrotMap map, int x, int y, int maxIterations) {
    map.delta[x][y] = 0;
    if (map.escaped[x][y]) {
      return map.iterations[x][y];
    }
    if (map.iterations[x][y] >= maxIterations) {
      return map.iterations[x][y];
    }

    double modSq;
    double cr = map.r0[x];
    double ci = map.i0[y];
    boolean escaped = false;

    double rt = map.r[x][y];
    double rtSq = rt * rt;
    
    double i = map.i[x][y];
    double iSq = i * i;
    do {
      map.r[x][y] = rtSq - iSq + cr;
      map.i[x][y] = 2 * rt * map.i[x][y] + ci;
      map.iterations[x][y]++;
      map.delta[x][y]++;

      rt = map.r[x][y];
      rtSq = rt * rt;
      iSq = map.i[x][y] * map.i[x][y];
      modSq = (rtSq + iSq);
      if (modSq > 4.0) {
        escaped = true;
      }
    } while (map.iterations[x][y] < maxIterations && !escaped);
    if (escaped) {
      map.escaped[x][y] = true;
    }
    return map.iterations[x][y];
  }

  public int solvePerimiter(MandelbrotMap map, Rectangle bounds, int maxIterations) {
    int min = Integer.MAX_VALUE;
    int max = 0;
    int its;
    for (int offset = 0 ; offset < bounds.width; offset++) {
      its = iterateUntil(map, bounds.x + offset, bounds.y, maxIterations);
      min = Math.min(its, min);
      max = Math.max(its, max);
      
      its = iterateUntil(map, bounds.x + offset, bounds.y + bounds.width - 1, maxIterations);
      min = Math.min(its, min);
      max = Math.max(its, max);
    }
    for (int offset = 0 ; offset < bounds.height; offset++) {
      its = iterateUntil(map, bounds.x, bounds.y + offset, maxIterations);
      min = Math.min(its, min);
      max = Math.max(its, max);
      its = iterateUntil(map, bounds.x + bounds.width - 1, bounds.y + offset, maxIterations);
      min = Math.min(its, min);
      max = Math.max(its, max);
    }
    int detail = max-min;
    return detail;
  }
}
