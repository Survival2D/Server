package survival2d.controller.client.lobby.response;

import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@EzyObjectBinding
public class CreateTeamResponse {

  long teamId;
}
