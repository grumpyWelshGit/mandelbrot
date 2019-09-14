package uk.org.landeg.mandel;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Mathlib {
  public static  PrimeContext getPrimeFactors(int n) {
    final Set<Integer> primes = new PrimeSieve(n).generatePrimes();
    final List<Integer> factors = new ArrayList<>();
    int val = n;
    for (int prime : primes) {
      while (val % prime == 0) {
        factors.add(prime);
        val /= prime;
      }
    }
    Collections.sort(factors);
    Collections.reverse(factors);
    final PrimeContext context = new PrimeContext(factors);
    return context;
  }

  public static int highestPrimeFactor(final PrimeContext context, int n) {
    return context.highestPrimeFactors.computeIfAbsent(n, 
        arg -> {
          for (int prime : context.primeFactors) {
            if (n % prime == 0) {
              return prime;
            }
          }
          return 1;
        }
      );
  }

  public static List<Rectangle> split(final Rectangle source, final PrimeContext context) {
    // for now - assume map is square.
    int subcells = highestPrimeFactor(context, source.width);
    final List<Rectangle> result = new ArrayList<>(subcells * subcells);
    int width = source.width / subcells;
    for (int x = 0 ; x < subcells ; x++) {
      for (int y = 0 ; y < subcells ; y++) {
        result.add(new Rectangle(source.x + x * width, source.y + y * width, width, width));
      }
    }
//    System.out.println("adding " + result.size() + "sub cells : " + result.toString());
    return result;
  }

  public static class PrimeContext {
    private List<Integer> primeFactors = new ArrayList<>();
    private Map<Integer, Integer> highestPrimeFactors = new HashMap<>();

    public PrimeContext(List<Integer> primeFactors) {
      this.primeFactors = primeFactors;
    }

    public List<Integer> getPrimeFactors() {
      return primeFactors;
    }

    public Map<Integer, Integer> getHighestPrimeFactors() {
      return highestPrimeFactors;
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("PrimeContext [primeFactors=").append(primeFactors).append(", highestPrimeFactors=")
          .append(highestPrimeFactors).append("]");
      return builder.toString();
    }
  }

  private static class PrimeSieve {
    final boolean[] sieve;
    int currentPrime = 0;

    public PrimeSieve(int factorLimit) {
      final int max = (int)Math.sqrt(factorLimit) + 1;
      sieve = new boolean[max];
      Arrays.fill(sieve, true);
      sieve[0] = sieve[1] = false; // by definition
    }

    void advanceToPrime() {
      do {
        currentPrime++;
      } while(currentPrime < sieve.length && !sieve[currentPrime]);
    }
    
    Set<Integer> generatePrimes() {
      final Set<Integer> primes = new LinkedHashSet<>();
      do {
        advanceToPrime();
        primes.add(currentPrime);
        markNonPrimes();
      } while (currentPrime < sieve.length);
      return primes;
    }

    private void markNonPrimes() {
      int nonPrime = currentPrime * 2;
      while (nonPrime < sieve.length) {
        sieve[nonPrime] = false;
        nonPrime += currentPrime;
      }
    }
  }
}
