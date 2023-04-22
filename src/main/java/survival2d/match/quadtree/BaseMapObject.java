package survival2d.match.quadtree;

import com.badlogic.gdx.math.Shape2D;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import survival2d.match.entity.base.MapObject;
import survival2d.match.util.MatchUtil;
import survival2d.util.serialize.GsonTransient;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Setter
@Slf4j
public class BaseMapObject extends BaseNode<BaseMapObject> implements MapObject {

  @GsonTransient protected Shape2D shape;

  @Override
  public boolean isCollision(BaseMapObject other) {
    return MatchUtil.isCollision(getShape(), other.getShape());
  }
}
