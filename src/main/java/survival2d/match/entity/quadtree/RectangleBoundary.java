package survival2d.match.entity.quadtree;

import survival2d.match.entity.base.Rectangle;

public class RectangleBoundary extends BaseBoundary {
  public RectangleBoundary(double x, double y, double width, double height) {
    super(x, y, new Rectangle(width, height));
  }

  @Override
  public Rectangle getShape() {
    return (Rectangle) shape;
  }
}
