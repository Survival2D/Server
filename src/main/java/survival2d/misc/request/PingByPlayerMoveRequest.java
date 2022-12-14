package survival2d.misc.request;

import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

@Value
@Slf4j
@EzyObjectBinding
public class PingByPlayerMoveRequest {
  Vector2D direction;
  double rotation;
}
