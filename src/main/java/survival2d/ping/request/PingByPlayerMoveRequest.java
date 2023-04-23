package survival2d.ping.request;

import com.badlogic.gdx.math.Vector2;
import lombok.Getter;

@Getter
public class PingByPlayerMoveRequest {
  Vector2 direction;
  double rotation;
}
