package uk.org.landeg.mandel;

import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Component;

import uk.org.landeg.mandel.Mathlib.PrimeContext;

@Component
public class MandelbrotContext {
  private BoundingBox bounds;
  private MandelbrotMap map;
  private int maxIterations;
  private PrimeContext primeContext;
  private AtomicReference<Integer> changeCount;

  public BoundingBox getBounds() {
    return bounds;
  }
  public void setBounds(BoundingBox bounds) {
    this.bounds = bounds;
  }
  public MandelbrotMap getMap() {
    return map;
  }
  public void setMap(MandelbrotMap map) {
    this.map = map;
  }
  public int getMaxIterations() {
    return maxIterations;
  }
  public void setMaxIterations(int maxIterations) {
    this.maxIterations = maxIterations;
  }
  public PrimeContext getPrimeContext() {
    return primeContext;
  }
  public void setPrimeContext(PrimeContext primeContext) {
    this.primeContext = primeContext;
  }
  public AtomicReference<Integer> getChangeCount() {
    return changeCount;
  }
  public void setChangeCount(AtomicReference<Integer> changeCount) {
    this.changeCount = changeCount;
  }
  
}
