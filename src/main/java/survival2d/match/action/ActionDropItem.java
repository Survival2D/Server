package survival2d.match.action;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ActionDropItem implements PlayerAction {

  String itemId;
}
