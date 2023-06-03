package survival2d.network.json.response;

import survival2d.network.json.JsonPacketId;

public class PingEmptyResponseJson extends BaseJsonResponse {

  public PingEmptyResponseJson() {
    id = JsonPacketId.PING_EMPTY;
    data = new Object();
  }
}
