package survival2d.game.action;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlayerDropItem implements PlayerAction {

  String itemId;
}
