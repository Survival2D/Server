package survival2d.match.entity.item;

import lombok.Getter;
import survival2d.match.entity.base.Item;
import survival2d.match.entity.config.ItemType;
import survival2d.match.entity.config.VestType;

@Getter
public class VestItem implements Item {

  final ItemType itemType = ItemType.VEST;
  final VestType vestType = VestType.LEVEL_0;
}
