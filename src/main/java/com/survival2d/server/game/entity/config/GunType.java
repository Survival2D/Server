package com.survival2d.server.game.entity.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GunType {
  NORMAL(BulletType.NORMAL, 1000); // TODO: fixme
  private final BulletType bulletType;
  private final int bulletCapacity;
}
