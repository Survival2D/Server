package survival2d.match.action;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlayerDropItem implements PlayerAction {

  String itemId;
}
