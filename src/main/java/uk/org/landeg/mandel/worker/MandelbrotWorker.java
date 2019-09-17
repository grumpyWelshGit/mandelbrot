package uk.org.landeg.mandel.worker;

import java.awt.Rectangle;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import uk.org.landeg.mandel.MandelbrotContext;
import uk.org.landeg.mandel.MandelbrotMap;
import uk.org.landeg.mandel.MandelbrotProcessor;
import uk.org.landeg.mandel.Mathlib;
import uk.org.landeg.mandel.PrimeContextHolder;

@Component
@Scope(proxyMode=ScopedProxyMode.TARGET_CLASS, scopeName=ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MandelbrotWorker implements Runnable {
  Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private MandelbrotContext context;

  @Autowired
  private MandelbrotProcessor processor;

  @Autowired
  BlockingQueue<Rectangle> queue;

  @Autowired
  PrimeContextHolder primeContextHolder;

  @Override
  public void run() {
    Rectangle cell;
    boolean notify = false;
    try {
      while ((cell = queue.take()) != null) {
        final MandelbrotMap map = context.getMap();
        notify = true;
        final int detail = processor.solvePerimiter(map, cell, context.getMaxIterations());
        if (detail > 0) {
          notify = splitCells(cell, queue);
        } else {
          fillCell(cell, map);
        }
        if (notify && queue.peek() == null) {
          synchronized (queue) {
            log.debug("notifying");
            queue.notifyAll();
          }
        }
      }
    } catch (InterruptedException e) {
      log.info("worker thread exiting");
    }
  }

  private boolean splitCells(Rectangle cell, BlockingQueue<Rectangle> queue) throws InterruptedException {
    boolean notify = true;
    final List<Rectangle> newCells = Mathlib.split(cell, primeContextHolder.getContext());
    if (!newCells.isEmpty()) {
      for (Rectangle c : newCells) {
        queue.put(c);
        notify = false;
      }
    }
    return notify;
  }

  private void fillCell(Rectangle cell, MandelbrotMap map) {
//    if (cell.width > 2) {
//      for (int dx = 0; dx < cell.getWidth(); dx++) {
//        for (int dy = 0; dy < cell.getHeight(); dy++) {
//          map.iterations[cell.x + dx][cell.y + dy] = map.iterations[cell.x][cell.y];
//        }
//      }
//    }
  }

}
