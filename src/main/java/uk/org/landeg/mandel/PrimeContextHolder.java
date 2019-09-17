package uk.org.landeg.mandel;

import org.springframework.stereotype.Component;

import uk.org.landeg.mandel.Mathlib.PrimeContext;

@Component
public class PrimeContextHolder {
  private PrimeContext context;

  public PrimeContext getContext() {
    return context;
  }

  public void setContext(PrimeContext context) {
    this.context = context;
  }
}
