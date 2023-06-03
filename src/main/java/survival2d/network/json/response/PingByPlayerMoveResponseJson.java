package survival2d.network.json.response;

import survival2d.network.json.JsonPacketId;
import survival2d.ping.data.SamplePingData;

public class PingByPlayerMoveResponseJson extends BaseJsonResponse {
  public PingByPlayerMoveResponseJson() {
    id = JsonPacketId.PING_BY_PLAYER_MOVE;
    data = SamplePingData.moveResponse;
  }
}
