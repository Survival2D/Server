package survival2d.match.entity.quadtree;

public class CircleBoundary extends BaseBoundary {
  public CircleBoundary(double x, double y, double radius) {
    super(x, y, new Circle(radius));
  }
}
