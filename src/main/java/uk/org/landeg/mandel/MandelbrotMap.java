package uk.org.landeg.mandel;

import java.awt.Point;
import java.awt.geom.Rectangle2D;

/**
 * Avoid storing points as objects, this is too resource hungry.
 * 
 * @author andy
 *
 */
public class MandelbrotMap {
  // coordinates of base map
  private double r0[];
  private double i0[];

  // current iterations
  private int iterations[][];
  
  // current evaluated z and r for each point
  private double r[][];
  private double i[][];

  private boolean escaped[][];

  public void initMap(Rectangle2D bounds, final Point steps) {
    final double rRange = bounds.getMaxX() - bounds.getMinX();
    final double rStep = rRange / (double) steps.getX();

    final double iRange = bounds.getMaxY() - bounds.getMinY();
    final double iStep = iRange / (double) steps.getY();
    
    escaped = new boolean[steps.x][steps.y];
    iterations = new int[steps.x][steps.y];

    // init r/x coords
    r0 = new double[steps.x];
    for (int x =  0  ; x < steps.x ; x++) {
      r0[x] = bounds.getMinX() + rStep * x;
    }

    // init i/y coords
    i0 = new double[steps.y];
    for (int y =  0  ; y < steps.y ; y++) {
      i0[y] = bounds.getMinY() + rStep * y;
    }

    r = new double[steps.x][steps.y];
    i = new double[steps.x][steps.y];

    for (int x =  0  ; x < rStep ; x++) {
      for (int y =  0  ; y < iStep ; y++) {
        escaped[x][y] = false;
        iterations[x][y] = 0;
        r[x][y] = 0;
        i[x][y] = 0;
      }
    }
  }

  public void iterateUntil(int x, int y, int maxIterations) {
    double modSq;
    double cr = r0[x];
    double ci = i0[y];

    do {
      double rt = r[x][y];
      r[x][y] = rt * rt - i[x][y] * i[x][y] + cr;
      i[x][y] = 2 * rt * i[x][y] + ci;
      modSq = (r[x][y] * r[x][y] + i[x][y] * i[x][y]);
      iterations[x][y]++;
    } while (iterations[x][y] <= maxIterations && modSq < 4.0);
    if (modSq >= 4.0) {
      escaped[x][y] = true;
    }
  }
  
  public int getIterations(int x, int y) {
    return iterations[x][y];
  }
}
