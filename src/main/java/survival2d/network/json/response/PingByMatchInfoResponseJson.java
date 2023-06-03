package survival2d.network.json.response;

import survival2d.match.entity.match.Match;
import survival2d.network.json.JsonPacketId;
import survival2d.ping.data.SamplePingData;
import survival2d.util.serialize.GsonHolder;

public class PingByMatchInfoResponseJson extends BaseJsonResponse {

  public PingByMatchInfoResponseJson() {
    id = JsonPacketId.PING_BY_MATCH_INFO;
    data = SamplePingData.match;
  }

  @Override
  public String toJson() {
    return GsonHolder.getWithExcludeAnnotation().toJson(this);
  }
}
