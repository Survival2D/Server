package com.survival2d.server.game.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum WeaponType {
  HAND(AttachType.MELEE),
  GUN(AttachType.RANGE);

  private final AttachType attachType;
}
