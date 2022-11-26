package survival2d.network.lobby.response;

import com.tvd12.ezyfox.binding.EzyMarshaller;
import com.tvd12.ezyfox.binding.EzyWriter;
import com.tvd12.ezyfox.binding.annotation.EzyWriterImpl;
import com.tvd12.ezyfox.entity.EzyHashMap;
import lombok.Builder;
import lombok.Data;
import lombok.val;
import org.locationtech.jts.math.Vector2D;
import survival2d.util.serialize.GsonHolder;

@Data
@Builder
public class PingByPlayerMoveResponse {
  Vector2D position;
  double rotation;

  @EzyWriterImpl
  public static class ResponseWriter implements EzyWriter<PingByPlayerMoveResponse, EzyHashMap> {

    @Override
    public EzyHashMap write(EzyMarshaller ezyMarshaller, PingByPlayerMoveResponse response) {
      val data = "{map: " + GsonHolder.getNormalGson().toJson(response) + "}";
      val map = GsonHolder.getNormalGson().fromJson(data, EzyHashMap.class);
      return map;
    }
  }
}
