package com.survival2d.server.game.entity.base;

public interface HasHp {

  double getHp();

  void setHp(double hp);

  default void reduceHp(double hp) {
    setHp(getHp() - hp);
  }
}
