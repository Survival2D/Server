package com.survival2d.server.game.entity.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BulletType {
  NORMAL(1, 100, 10);
  private final long damage;
  private final long maxRange;
  private final long damageRadius;
}
