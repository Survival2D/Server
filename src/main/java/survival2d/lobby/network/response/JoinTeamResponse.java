package survival2d.lobby.network.response;

import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;
import lombok.Builder;
import lombok.Data;
import survival2d.lobby.entity.JoinTeamResult;

@Data
@Builder
@EzyObjectBinding
public class JoinTeamResponse {

  JoinTeamResult result;
  long teamId;
}
