package survival2d.match.entity.item;

import lombok.Builder;
import lombok.Getter;
import survival2d.match.entity.HelmetType;
import survival2d.match.entity.base.Item;
import survival2d.match.entity.base.ItemType;

@Getter
@Builder
public class HelmetItem implements Item {

  final ItemType itemType = ItemType.HELMET;
  final HelmetType helmetType = HelmetType.LEVEL_0;
}
