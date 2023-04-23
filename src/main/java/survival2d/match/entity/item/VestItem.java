package survival2d.match.entity.item;

import lombok.Getter;
import lombok.NoArgsConstructor;
import survival2d.match.entity.base.Item;
import survival2d.match.type.ItemType;
import survival2d.match.type.VestType;

@Getter
@NoArgsConstructor
public class VestItem implements Item {

  final ItemType itemType = ItemType.VEST;
  VestType vestType = VestType.LEVEL_0;

  public VestItem(VestType vestType) {
    this.vestType = vestType;
  }
}
