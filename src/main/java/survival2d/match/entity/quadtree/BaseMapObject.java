package survival2d.match.entity.quadtree;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import survival2d.match.entity.base.MapObject;
import survival2d.match.entity.base.Shape;
import survival2d.util.math.MathUtil;

@Getter
@Setter
@Slf4j
public class BaseMapObject extends Node<BaseMapObject> implements MapObject {

  Shape shape;

  BaseMapObject(int id, Vector2D position, Shape shape) {
    super(id, position);
    this.shape = shape;
  }

  @Override
  public boolean isCollision(BaseMapObject other) {
    return MathUtil.isIntersect(getPosition(), getShape(), other.getPosition(), other.getShape());
  }
}
