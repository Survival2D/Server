package survival2d.match.entity.quadtree;

import com.badlogic.gdx.math.Shape2D;
import java.awt.Shape;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import survival2d.match.util.MatchUtil;
import survival2d.util.math.MathUtil;

@Getter
@Setter
@ToString
public abstract class BaseBoundary {
  Shape2D shape;

  public BaseBoundary(Shape shape) {
    this.shape = shape;
  }

  public boolean contains(Node node) {
    // Chứa node nếu chứa tâm của node đó
    return MatchUtil.isCollision( shape, node.getShape());
  }

  public boolean isIntersect(BaseBoundary other) {
    return MatchUtil.isCollision(shape, other.shape);
  }
}
