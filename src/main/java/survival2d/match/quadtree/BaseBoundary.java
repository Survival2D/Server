package survival2d.match.quadtree;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import survival2d.match.entity.base.Dot;
import survival2d.match.entity.base.Shape;
import survival2d.util.math.MathUtil;

@Getter
@Setter
@ToString
public abstract class BaseBoundary {
  Vector2D position;
  Shape shape;

  public BaseBoundary(double x, double y, Shape shape) {
    position = new Vector2D(x, y);
    this.shape = shape;
  }

  public boolean contains(Node node) {
    // Chứa node nếu chứa tâm của node đó
    return MathUtil.isIntersect(position, shape, node.getPosition(), Dot.DOT);
  }

  public boolean isIntersect(BaseBoundary other) {
    return MathUtil.isIntersect(position, shape, other.position, other.shape);
  }
}
