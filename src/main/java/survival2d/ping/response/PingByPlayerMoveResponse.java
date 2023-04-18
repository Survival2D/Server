package survival2d.ping.response;

import com.tvd12.ezyfox.binding.EzyMarshaller;
import com.tvd12.ezyfox.binding.EzyWriter;
import com.tvd12.ezyfox.binding.annotation.EzyWriterImpl;
import com.tvd12.ezyfox.entity.EzyHashMap;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import survival2d.util.serialize.GsonHolder;

@Data
@Builder
@Slf4j
public class PingByPlayerMoveResponse {

  String username;
  Vector2 position;
  double rotation;

  @EzyWriterImpl
  public static class ResponseWriter implements EzyWriter<PingByPlayerMoveResponse, EzyHashMap> {

    @Override
    public EzyHashMap write(EzyMarshaller ezyMarshaller, PingByPlayerMoveResponse response) {
      var data = "{map: " + GsonHolder.getNormalGson().toJson(response) + "}";
      log.info("PingByPlayerMoveResponse's length: {}", data.length());
      var map = GsonHolder.getNormalGson().fromJson(data, EzyHashMap.class);
      return map;
    }
  }
}
