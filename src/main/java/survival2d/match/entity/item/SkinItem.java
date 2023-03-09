package survival2d.match.entity.item;

import lombok.Builder;
import lombok.Getter;
import survival2d.match.entity.SkinType;
import survival2d.match.entity.base.Item;
import survival2d.match.entity.base.ItemType;

@Getter
@Builder
public class SkinItem implements Item {

  final ItemType itemType = ItemType.SKIN;
  final SkinType skinType = SkinType.DEFAULT;
}
