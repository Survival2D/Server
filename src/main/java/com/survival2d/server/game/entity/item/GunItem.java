package com.survival2d.server.game.entity.item;

import com.survival2d.server.game.entity.WeaponType;
import com.survival2d.server.game.entity.base.Item;
import com.survival2d.server.game.entity.base.ItemType;
import com.survival2d.server.game.entity.config.GunType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GunItem implements Item {

  final ItemType itemType = ItemType.WEAPON;
  final WeaponType weaponType = WeaponType.GUN;
  GunType gunType;
  long numBullet;
}
