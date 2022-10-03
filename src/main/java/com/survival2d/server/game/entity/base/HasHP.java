package com.survival2d.server.game.entity.base;

public interface HasHP {

  double getHP();

  void setHP(double hp);

  void reduceHp(double hp);
}
