package survival2d.network.json.response;

import survival2d.match.entity.match.Match;
import survival2d.network.json.JsonPacketId;
import survival2d.ping.data.SamplePingData;

public class PingByMatchInfoResponseJson extends BaseJsonResponse {
  protected Match match = SamplePingData.match;

  public PingByMatchInfoResponseJson() {
    id = JsonPacketId.PING_BY_MATCH_INFO;
  }
}
