package survival2d.match.entity.item;

import lombok.Getter;
import survival2d.match.entity.base.Item;
import survival2d.match.type.ItemType;

@Getter
public class MedKitItem implements Item {

  final ItemType itemType = ItemType.MEDKIT;
}
