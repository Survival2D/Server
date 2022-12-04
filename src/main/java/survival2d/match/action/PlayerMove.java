package survival2d.match.action;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.locationtech.jts.math.Vector2D;

@Builder
@Getter
@AllArgsConstructor
public class PlayerMove implements PlayerAction {
  private Vector2D direction;
  private double rotation;
}
