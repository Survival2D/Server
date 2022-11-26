package survival2d.network.lobby.response;

import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@EzyObjectBinding
@Deprecated
public class PingResponse {
  long time;
}
