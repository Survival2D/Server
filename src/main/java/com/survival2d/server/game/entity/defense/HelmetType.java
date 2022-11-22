package com.survival2d.server.game.entity.defense;

import com.survival2d.server.game.entity.base.ItemType;
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
