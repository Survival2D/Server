package com.survival2d.server.game.entity;

public abstract class Weapon {


  public abstract WeaponType getWeaponType();

  public AttachType getAttachType() {
    return getWeaponType().getAttachType();
  }
}
