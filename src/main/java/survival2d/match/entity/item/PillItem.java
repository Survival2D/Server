package survival2d.match.entity.item;

import lombok.Getter;
import survival2d.match.entity.base.Item;
import survival2d.match.entity.config.ItemType;

@Getter
public class PillItem implements Item {

  final ItemType itemType = ItemType.PILL;
}
