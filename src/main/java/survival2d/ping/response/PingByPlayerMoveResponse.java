package survival2d.ping.response;

import com.badlogic.gdx.math.Vector2;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PingByPlayerMoveResponse {

  int playerId;
  Vector2 position;
  double rotation;
}
