package survival2d.network.lobby.request;

import com.tvd12.ezyfox.binding.EzyReader;
import com.tvd12.ezyfox.binding.EzyUnmarshaller;
import com.tvd12.ezyfox.binding.annotation.EzyReaderImpl;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.math.Vector2D;
import survival2d.util.serialize.GsonHolder;

@Value
@Slf4j
public class PingByPlayerMoveRequest {
  Vector2D direction;
  double rotation;

  @EzyReaderImpl
  public static class RequestReader implements EzyReader<Object, PingByPlayerMoveRequest> {
    @Override
    public PingByPlayerMoveRequest read(EzyUnmarshaller ezyUnmarshaller, Object o) {
      log.info("PingByPlayerMoveRequest's length: {}", o.toString().length());
      return GsonHolder.getNormalGson().fromJson(o.toString(), PingByPlayerMoveRequest.class);
    }
  }
}
