package com.survival2d.server.game.entity.weapon;

import com.survival2d.server.game.entity.Weapon;
import com.survival2d.server.game.entity.WeaponType;
import lombok.Getter;

@Getter
public class Hand extends Weapon {
  private final WeaponType weaponType = WeaponType.HAND;
}
