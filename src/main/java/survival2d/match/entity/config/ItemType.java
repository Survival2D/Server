package survival2d.match.entity.config;

import survival2d.flatbuffers.Item;

public enum ItemType {
  WEAPON,
  BULLET,
  SKIN,
  BACKPACK,
  HELMET,
  VEST,
  BANDAGE,
  MEDKIT,
  SODA,
  PILL,
  UNKNOWN; //For parse check

  public static ItemType parse(int itemId) {
    if (itemId < 0 || itemId >= Item.names.length) {
      return UNKNOWN;
    }
    return ItemType.values()[itemId];
  }
}
