package survival2d.match.entity.item;

import lombok.Builder;
import lombok.Getter;
import survival2d.match.entity.base.Item;
import survival2d.match.entity.base.ItemType;

@Getter
@Builder
public class MedKitItem implements Item {

  final ItemType itemType = ItemType.MED_KIT;
}
