package com.survival2d.server.game.entity.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BulletType {
  NORMAL(10, 1000, 10, 20);
  private final long damage;
  private final long maxRange;
  private final long damageRadius;
  private final long speed;
}
