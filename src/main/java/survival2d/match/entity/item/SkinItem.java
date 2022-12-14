package survival2d.match.entity.item;

import lombok.Getter;
import survival2d.match.entity.base.Item;
import survival2d.match.entity.config.ItemType;
import survival2d.match.entity.config.SkinType;

@Getter
public class SkinItem implements Item {

  final ItemType itemType = ItemType.SKIN;
  final SkinType skinType = SkinType.DEFAULT;
}
