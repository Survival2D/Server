package survival2d.match.entity.defense;

import lombok.AllArgsConstructor;
import lombok.Getter;
import survival2d.match.entity.base.Item;
import survival2d.match.entity.base.ItemType;

@AllArgsConstructor
@Getter
public enum VestType implements Item {
  VEST_1(1);
  private final ItemType itemType = ItemType.VEST;
  int armor;
}
