package survival2d.match.entity.quadtree;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.junit.jupiter.api.Test;
import survival2d.match.entity.base.MapObject;
import survival2d.match.entity.obstacle.Container;

class QuadTreeTest {
  @Test
  public void checkCollision() {
    QuadTree<MapObject> map = new QuadTree<>(0, 0, 1000, 1000);
    var container = new Container();
    container.setPosition(new Vector2(100, 100));
    map.add(container);
    var result = map.query(new Rectangle(50, 50, 100, 100));
    assertEquals(1, result.size());
  }
}
