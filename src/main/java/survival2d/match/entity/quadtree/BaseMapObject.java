package survival2d.match.entity.quadtree;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import survival2d.match.entity.base.MapObject;
import survival2d.match.util.MatchUtil;

@Getter
@NoArgsConstructor
@Setter
@Slf4j
public abstract class BaseMapObject extends BaseNode<BaseMapObject> implements MapObject {

  @Override
  public boolean isIntersect(BaseMapObject other) {
    return MatchUtil.isIntersect(getShape(), other.getShape());
  }
}
