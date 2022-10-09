package com.survival2d.server.game.entity.weapon;

import com.survival2d.server.game.entity.AttachType;
import com.survival2d.server.game.entity.Weapon;

public class MeleeWeapon extends Weapon {

  @Override
  public final AttachType getAttachType() {
    return AttachType.MELEE;
  }
}
