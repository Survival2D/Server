package survival2d.match.action;

import com.badlogic.gdx.math.Vector2;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;



@Builder
@Getter
@AllArgsConstructor
public class PlayerMove implements PlayerAction {

  private Vector2 direction;
  private double rotation;
}
