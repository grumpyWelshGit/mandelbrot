package uk.org.landeg.mandel;

import java.awt.Rectangle;

public class MandelbrotProcessor {
  public int iterateUntil(MandelbrotMap map, int x, int y, int maxIterations) {
    if (map.escaped[x][y]) {
      return map.iterations[x][y];
    }
    if (map.iterations[x][y] >= maxIterations) {
      return map.iterations[x][y];
    }

    double modSq;
    double cr = map.r0[x];
    double ci = map.i0[y];

    do {
      double rt = map.r[x][y];
      map.r[x][y] = rt * rt - map.i[x][y] * map.i[x][y] + cr;
      map.i[x][y] = 2 * rt * map.i[x][y] + ci;
      modSq = (map.r[x][y] * map.r[x][y] + map.i[x][y] * map.i[x][y]);
      map.iterations[x][y]++;
    } while (map.iterations[x][y] < maxIterations && modSq < 4.0);
    if (modSq >= 4.0) {
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
