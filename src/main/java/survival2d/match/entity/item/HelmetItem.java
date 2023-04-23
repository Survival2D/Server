package survival2d.match.entity.item;

import lombok.Getter;
import lombok.NoArgsConstructor;
import survival2d.match.entity.base.Item;
import survival2d.match.type.HelmetType;
import survival2d.match.type.ItemType;

@Getter
@NoArgsConstructor
public class HelmetItem implements Item {

  final ItemType itemType = ItemType.HELMET;
  HelmetType helmetType = HelmetType.LEVEL_0;

  public HelmetItem(HelmetType helmetType) {
    this.helmetType = helmetType;
  }
}
