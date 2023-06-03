package survival2d.network.json.response;

import com.badlogic.gdx.math.Vector2;
import survival2d.network.json.JsonPacketId;
import survival2d.ping.data.SamplePingData;

public class PingByPlayerMoveResponseJson extends BaseJsonResponse {
  protected Vector2 position = SamplePingData.position;
  protected float rotation = SamplePingData.rotation;

  public PingByPlayerMoveResponseJson() {
    id = JsonPacketId.PING_BY_PLAYER_MOVE;
  }
}
