package uk.org.landeg.mandel;

import java.awt.Point;
import java.awt.Rectangle;

import org.junit.Assert;
import org.junit.Test;

import uk.org.landeg.mandel.Mathlib.PrimeContext;

public class MandelProcessorTest {
  @Test
  public void assertIterate() {
    final MandelbrotMap map = new MandelbrotMap();
    final BoundingBox box = new BoundingBox(-0.1, -0.1, 0.2, 0.2);
    map.initMap(box, new Point(3,3));
    int it1 = new MandelbrotProcessor().iterateUntil(map, 0, 0, 200);
    int it2 = new MandelbrotProcessor().iterateUntil(map, 0, 0, 200);
    Assert.assertEquals(it1, it2);
  }

  @Test
  public void assertBoxSolution() {
    final int mapSize = 1800;
    final PrimeContext primeContext = Mathlib.getPrimeFactors(mapSize);
    final MandelbrotMap map = new MandelbrotMap();
    final MandelbrotProcessor processor = new MandelbrotProcessor();

    final BoundingBox bounds = new BoundingBox(0.0, 0.0, 2, 2);
    map.initMap(bounds, new Point(mapSize, mapSize));

    final Rectangle cell = new Rectangle(1792,1792, 8, 8);
    final int maxIterations = 200;
    final int detail = processor.solvePerimiter(map, cell, maxIterations);
    Assert.assertEquals(0, detail);
  }
}
