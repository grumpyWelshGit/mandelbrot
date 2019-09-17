package uk.org.landeg.mandel.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
import uk.org.landeg.mandel.MandelbrotService;

@Component
public class FractalViewerFrame extends JFrame {
  
  @Autowired
  private MandelbrotMap map;

  @Autowired
  private MandelbrotService service;

  transient Logger log = LoggerFactory.getLogger(this.getClass());

  private static final long serialVersionUID = 2332924833609414030L;
  private transient BufferedImage image = null;
  private transient BufferedImage overlayImage = null;
  private transient BufferedImage osi = null;
  
  private double coordsx, coordsy;
  private int iterations;

  public FractalViewerFrame() {
    this.setSize(1024, 1024);
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
        if (map == null) {
          return;
        }
        coordsx = map.r0[e.getX()];
        coordsy = map.i0[e.getY()];
        iterations = map.iterations[e.getX()][e.getY()];
        repaint();
      }
    });

    this.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        service.render(map.r0[e.getX()], map.i0[e.getY()], map.iRange / 2);
      }
    });
    this.addMouseWheelListener(new MouseWheelListener() {
      @Override
      public void mouseWheelMoved(MouseWheelEvent e) {
      }
    });
    
    this.addKeyListener(new KeyListener() {
      @Override
      public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == '+') {
          service.increaseDepth();
          return;
        }
        if (e.getKeyChar() == '-') {
          service.decreaseDepth();
          return;
        }
        if (e.getKeyChar() == 'r') {
          service.renderDefault();
          return;
        }
        if (e.getKeyChar() == 'f') {
          service.repaint();
          return;
        }
      }
      
      @Override
      public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub
        
      }
      
      @Override
      public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub
        
      }
    });
  }

  public void setImage(BufferedImage image) {
    this.image = image;
    if (overlayImage == null) {
      this.overlayImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
      this.osi = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
    }
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
