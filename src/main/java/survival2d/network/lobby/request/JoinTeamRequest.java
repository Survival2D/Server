package survival2d.network.lobby.request;

import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;
import lombok.Data;

@Data
@EzyObjectBinding
public class JoinTeamRequest {

  int teamId; // TeamId = -1 là join team bất kỳ còn slot
}
