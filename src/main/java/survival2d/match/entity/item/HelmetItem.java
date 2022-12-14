package survival2d.match.entity.item;

import lombok.Getter;
import survival2d.match.entity.base.Item;
import survival2d.match.entity.config.HelmetType;
import survival2d.match.entity.config.ItemType;

@Getter
public class HelmetItem implements Item {

  final ItemType itemType = ItemType.HELMET;
  final HelmetType helmetType = HelmetType.LEVEL_0;
}
