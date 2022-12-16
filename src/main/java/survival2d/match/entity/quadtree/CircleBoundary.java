package survival2d.match.entity.quadtree;

import survival2d.match.entity.base.Circle;

public class CircleBoundary extends Boundary {
  public CircleBoundary(double x, double y, double radius) {
    super(x, y, new Circle(radius));
  }
}
