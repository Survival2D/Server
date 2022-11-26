package survival2d.network.match.request;

import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;
import lombok.Data;

@Data
@EzyObjectBinding
public class MatchInfoRequest {

  private long matchId;
}
