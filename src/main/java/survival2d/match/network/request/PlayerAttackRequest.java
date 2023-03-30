package survival2d.match.network.request;

import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;
import lombok.Data;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2;


@Data
@EzyObjectBinding
public class PlayerAttackRequest {

  private Vector2 direction;
}
