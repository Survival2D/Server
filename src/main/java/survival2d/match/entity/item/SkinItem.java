package survival2d.match.entity.item;

import lombok.Getter;
import survival2d.match.entity.base.Item;
import survival2d.match.type.ItemType;
import survival2d.match.type.SkinType;

@Getter
public class SkinItem implements Item {

  final ItemType itemType = ItemType.SKIN;
  final SkinType skinType = SkinType.DEFAULT;
}
