package uk.org.landeg.mandel.ui;

import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import org.springframework.stereotype.Component;

@Component
public class FractalViewerFrame extends JFrame {
  private static final long serialVersionUID = 2332924833609414030L;
  private BufferedImage image = null;

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
  }

  public void setImage(BufferedImage image) {
    this.image = image;
    this.repaint();
  }

  @Override
  public void paint(Graphics g) {
    if (image != null) {
      g.drawImage(image, 0, 0, this);
    }
  }
}
