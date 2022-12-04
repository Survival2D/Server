package survival2d.controller.client.misc.request;

import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.math.Vector2D;

@Value
@Slf4j
@EzyObjectBinding
public class PingByPlayerMoveRequest {
  Vector2D direction;
  double rotation;
}
