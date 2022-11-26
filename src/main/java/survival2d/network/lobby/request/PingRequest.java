package survival2d.network.lobby.request;

import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;
import lombok.Data;

@Data
@EzyObjectBinding
@Deprecated
public class PingRequest {
  long time;
}
