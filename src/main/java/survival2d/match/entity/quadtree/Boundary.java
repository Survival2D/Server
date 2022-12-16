package survival2d.match.entity.quadtree;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import survival2d.match.entity.base.Dot;
import survival2d.match.entity.base.Shape;
import survival2d.util.math.MathUtil;

@Getter
@Setter
public abstract class Boundary {
  Vector2D position;
  Shape shape;

  public Boundary(double x, double y, Shape shape) {
    position = new Vector2D(x, y);
    this.shape = shape;
  }

  public boolean contains(Node<?> object) {
    // Chứa object nếu chứa tâm của object đó
    return MathUtil.isIntersect(position, shape, object.getPosition(), Dot.DOT);
  }

  public boolean isIntersect(Boundary other) {
    return MathUtil.isIntersect(position, shape, other.position, other.shape);
  }
}
