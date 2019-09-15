package uk.org.landeg.mandel.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.org.landeg.mandel.MandelbrotMap;

@Component
public class FractalViewerFrame extends JFrame {
  
  @Autowired
  private MandelbrotMap map;

  transient Logger log = LoggerFactory.getLogger(this.getClass());

  private static final long serialVersionUID = 2332924833609414030L;
  private transient BufferedImage image = null;
  private transient BufferedImage overlayImage = null;
  private transient BufferedImage osi = null;
  
  private double coordsx, coordsy;
  private int iterations;

  public FractalViewerFrame() {
    this.setSize(1000, 1000);
    this.setVisible(true);
    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        super.windowClosing(e);
        System.exit(0);
      }
    });
    this.addMouseMotionListener(new MouseMotionAdapter() {
      @Override
      public void mouseDragged(MouseEvent e) {
        log.debug("mouse drag event {} {}", e.getX(), e.getY());
      }
      
      @Override
      public void mouseMoved(MouseEvent e) {
        log.debug("mouse moved {} {}", e.getX(), e.getY());
        coordsx = map.r0[e.getX()];
        coordsy = map.i0[e.getY()];
        iterations = map.iterations[e.getX()][e.getY()];
        repaint();
      }
    });

    this.addMouseListener(new MouseAdapter() {
      
    });
    this.addMouseWheelListener(new MouseWheelListener() {
      @Override
      public void mouseWheelMoved(MouseWheelEvent e) {
      }
    });
  }

  public void setImage(BufferedImage image) {
    this.image = image;
    this.overlayImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
    this.osi = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
    this.repaint();
  }


  @Override
  public void repaint() {
    this.paint(this.getGraphics());
  }

  @Override
  public void paint(Graphics g) {
    if (osi == null) {
      return;
    }
    final Graphics osg = osi.getGraphics();
    if (image != null) {
      osg.drawImage(image, 0, 0, this);
    }
    paintOverlay(osg);
    g.drawImage(osi, 0, 0, this);
    osg.dispose();
  }

  private void paintOverlay(Graphics g) {
    if (overlayImage == null) {
      return;
    }
    final Graphics osg = overlayImage.getGraphics();
    osg.setColor(Color.BLACK);
    osg.fillRect(0, 0, overlayImage.getWidth(), 60);
    osg.setColor(Color.WHITE);
    osg.drawString(String.format("Coords[%f,%f] : %d", coordsx, coordsy, iterations), 100, 50);
    osg.dispose();
    g.drawImage(overlayImage, 0, 0, this);
  }
}
