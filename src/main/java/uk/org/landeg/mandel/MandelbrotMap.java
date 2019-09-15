package uk.org.landeg.mandel;

import java.awt.Point;

/**
 * Avoid storing points as objects, this is too resource hungry.
 * 
 * @author andy
 *
 */
public class MandelbrotMap {
  // coordinates of base map
  public double r0[];
  public double i0[];

  // current iterations
  public int iterations[][];
  
  // current evaluated z and r for each point
  public double r[][];
  public double i[][];

  public boolean escaped[][];
  public int sizeX;
  public int sizeY;

  public void initMap(BoundingBox bounds, final Point steps) {
    final double rRange = bounds.getMaxX() - bounds.getMinX();
    final double rStep = rRange / (double) steps.getX();

    final double iRange = bounds.getMaxY() - bounds.getMinY();
    final double iStep = iRange / (double) steps.getY();
    
    escaped = new boolean[steps.x][steps.y];
    iterations = new int[steps.x][steps.y];
    sizeX = steps.x;
    sizeY = steps.y;

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

  public int getIterations(int x, int y) {
    return iterations[x][y];
  }
}
