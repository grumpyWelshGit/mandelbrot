package uk.org.landeg.mandel;

import org.junit.Assert;
import org.junit.Test;

import uk.org.landeg.mandel.Mathlib.PrimeContext;

public class MathLibTest {
  @Test
  public void testSieve() {
    System.out.println(Mathlib.getPrimeFactors(1800));
  }

  @Test
  public void testHighestFactor() {
    final PrimeContext context = Mathlib.getPrimeFactors(1800);
    Assert.assertEquals(5, Mathlib.highestPrimeFactor(context, 1800));
    Assert.assertEquals(1, context.getHighestPrimeFactors().size());
    System.out.println(context);
    Assert.assertEquals(5, Mathlib.highestPrimeFactor(context, 320));
    Assert.assertEquals(2, context.getHighestPrimeFactors().size());
    Assert.assertEquals(5, Mathlib.highestPrimeFactor(context, 320));
    Assert.assertEquals(2, context.getHighestPrimeFactors().size());
    Assert.assertEquals(2, Mathlib.highestPrimeFactor(context, 64));
    Assert.assertEquals(3, context.getHighestPrimeFactors().size());
    System.out.println(context);
  }
}
