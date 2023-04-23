package survival2d.match.entity.quadtree;

import com.badlogic.gdx.math.Rectangle;

public class RectangleBoundary extends BaseBoundary {
  public RectangleBoundary(double x, double y, double width, double height) {
    super(x, y, new Rectangle(width, height));
  }

  @Override
  public Rectangle getShape() {
    return (Rectangle) shape;
  }
}
