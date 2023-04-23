package survival2d.match.entity.obstacle;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import survival2d.match.entity.base.Destroyable;
import survival2d.match.entity.base.HasHp;
import survival2d.match.entity.quadtree.BaseMapObject;
import survival2d.util.serialize.GsonTransient;

@Getter
@Setter
public class Tree extends BaseMapObject implements Destroyable, HasHp, Obstacle {

  private static final Circle ROOT_SHAPE = new Circle(50);
  private static final Circle FOLIAGE_SHAPE = new Circle(150);
  @GsonTransient int id;
  @GsonTransient double hp = 100;
  Vector2 position;
  @GsonTransient Circle shape = ROOT_SHAPE;
  @GsonTransient Circle foliage = FOLIAGE_SHAPE;
  ObstacleType type = ObstacleType.TREE;

  @Override
  public boolean isDestroyed() {
    return HasHp.super.isDestroyed();
  }

  @Override
  public void setDestroyed(boolean destroyed) {}
}
