package survival2d.match.entity.item;

import lombok.Getter;
import survival2d.match.entity.base.Item;
import survival2d.match.entity.config.BackPackType;
import survival2d.match.entity.config.ItemType;

@Getter
public class BackPackItem implements Item {

  final ItemType itemType = ItemType.BACKPACK;
  final BackPackType backPackType = BackPackType.LEVEL_0;
}
