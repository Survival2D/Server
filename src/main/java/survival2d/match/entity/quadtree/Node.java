package survival2d.match.entity.quadtree;

import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;

public interface Node {

  int getId();

  Vector2 getPosition();

  Shape2D getShape();
}
