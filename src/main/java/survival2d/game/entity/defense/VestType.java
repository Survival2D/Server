package survival2d.game.entity.defense;

import survival2d.game.entity.base.Item;
import survival2d.game.entity.base.ItemType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum VestType implements Item {
  VEST_1(1);
  int armor;
  private final ItemType itemType = ItemType.VEST;
}
