package survival2d.match.action;

import com.badlogic.gdx.math.Vector2;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;



@Builder
@Getter
@AllArgsConstructor
public class ActionMove implements PlayerAction {

  private Vector2 direction;
  private float rotation;
}
