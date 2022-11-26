package survival2d.game.entity.defense;

import survival2d.game.entity.base.ItemType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum HelmetType {
  HELMET_1(5),

  HELMET_2(10),
  HELMET_3(15);
  private final ItemType itemType = ItemType.HELMET;
  int armor;
}
