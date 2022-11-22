package com.survival2d.server.game.entity.defense;

import com.survival2d.server.game.entity.base.Item;
import com.survival2d.server.game.entity.base.ItemType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum VestType implements Item {
  VEST_1(1);
  int armor;
  private final ItemType itemType = ItemType.VEST;
}
