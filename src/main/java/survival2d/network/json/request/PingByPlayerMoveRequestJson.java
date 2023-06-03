package survival2d.network.json.request;

import com.badlogic.gdx.math.Vector2;
import lombok.Getter;

@Getter
public class PingByPlayerMoveRequestJson extends BaseJsonRequest {
  Vector2 direction;
  float rotation;
}
