package survival2d.match.entity.quadtree;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import survival2d.match.entity.base.MapObject;
import survival2d.match.entity.base.Shape;
import survival2d.util.math.MathUtil;
import survival2d.util.serialize.ExcludeFromGson;

@AllArgsConstructor
@Getter
@NoArgsConstructor
//@RequiredArgsConstructor
@Setter
@Slf4j
public class BaseMapObject extends BaseNode<BaseMapObject> implements MapObject {

  @ExcludeFromGson protected Shape shape;

  @Override
  public boolean isCollision(BaseMapObject other) {
    return MathUtil.isIntersect(getPosition(), getShape(), other.getPosition(), other.getShape());
  }
}
