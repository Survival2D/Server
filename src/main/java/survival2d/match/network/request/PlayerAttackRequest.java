package survival2d.match.network.request;

import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;
import lombok.Data;



@Data
@EzyObjectBinding
public class PlayerAttackRequest {

  private Vector2 direction;
}
