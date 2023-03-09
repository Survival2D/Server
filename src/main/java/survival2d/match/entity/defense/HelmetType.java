package survival2d.match.entity.defense;

import lombok.AllArgsConstructor;
import lombok.Getter;
import survival2d.match.entity.base.ItemType;

@AllArgsConstructor
@Getter
public enum HelmetType {
  HELMET_1(5),

  HELMET_2(10),
  HELMET_3(15);
  private final ItemType itemType = ItemType.HELMET;
  int armor;
}
