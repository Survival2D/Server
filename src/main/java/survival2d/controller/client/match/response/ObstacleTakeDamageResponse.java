package survival2d.controller.client.match.response;

import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@EzyObjectBinding
public class ObstacleTakeDamageResponse {

  private long obstacleId;
  private double hp;
}
