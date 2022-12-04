package survival2d.match.network.request;

import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;
import lombok.Data;
import org.locationtech.jts.math.Vector2D;


@Data
@EzyObjectBinding
public class PlayerAttackRequest {

  private Vector2D direction;
}
