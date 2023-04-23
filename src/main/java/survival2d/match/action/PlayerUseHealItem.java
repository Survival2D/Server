package survival2d.match.action;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PlayerUseHealItem implements PlayerAction {
  private final int itemId;

  public PlayerUseHealItem(int itemId) {
    this.itemId = itemId;
  }
}
