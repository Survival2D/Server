package survival2d.match.entity.quadtree;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Setter
public abstract class BaseNode<T extends BaseNode<?>> implements Node {
  protected int id;
  protected Vector2D position;

  public abstract boolean isCollision(T other);
}
