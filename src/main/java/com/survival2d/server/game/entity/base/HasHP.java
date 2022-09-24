package com.survival2d.server.game.entity.base;


import com.survival2d.server.game.entity.Property;

public interface HasHP extends MapObject {

  default double getHP() {
    return (double) get(Property.HP);
  }

  default void setHP(double hp) {
    set(Property.HP, hp);
  }
}
