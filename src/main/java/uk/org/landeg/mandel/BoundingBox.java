package uk.org.landeg.mandel;

public class BoundingBox {
  private final double x;
  private final double y;
  private final double width;
  private final double height;
  private final double maxX;
  private final double maxY;

  public BoundingBox(double x, double y, double width, double height) {
    super();
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.maxX = x + width;
    this.maxY = y + height;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double getWidth() {
    return width;
  }

  public double getHeight() {
    return height;
  }

  public double getMaxX() {
    return maxX;
  }

  public double getMaxY() {
    return maxY;
  }

  public double getMinX() {
    return x;
  }

  public double getMinY() {
    return y;
  }
}
