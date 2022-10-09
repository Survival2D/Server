package com.survival2d.server.game.entity.weapon;

import com.survival2d.server.game.entity.AttachType;
import com.survival2d.server.game.entity.Weapon;

public class RangeWeapon extends Weapon {

  @Override
  public final AttachType getAttachType() {
    return AttachType.RANGE;
  }
}
