package survival2d.match.entity.quadtree;

import com.badlogic.gdx.math.Vector2;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Setter
public abstract class BaseNode<T extends BaseNode<?>> implements Node {
  protected int id;
  protected Vector2 position;

  public Vector2 getPosition() {
    return position.cpy();
  }
}
