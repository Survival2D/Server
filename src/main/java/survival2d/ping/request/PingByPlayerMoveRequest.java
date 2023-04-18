package survival2d.ping.request;

import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;



@Value
@Slf4j
@EzyObjectBinding
public class PingByPlayerMoveRequest {

  Vector2 direction;
  double rotation;
}
