package survival2d.network.lobby.response;

import survival2d.network.lobby.entity.JoinTeamResult;
import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@EzyObjectBinding
public class JoinTeamResponse {

  JoinTeamResult result;
  long teamId;
}
