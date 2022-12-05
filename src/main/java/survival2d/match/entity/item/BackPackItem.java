package survival2d.match.entity.item;

import lombok.Builder;
import lombok.Getter;
import survival2d.match.entity.BackPackType;
import survival2d.match.entity.base.Item;
import survival2d.match.entity.base.ItemType;

@Getter
@Builder
public class BackPackItem implements Item {

  final ItemType itemType = ItemType.BACKPACK;
  final BackPackType backPackType = BackPackType.LEVEL_0;
}
