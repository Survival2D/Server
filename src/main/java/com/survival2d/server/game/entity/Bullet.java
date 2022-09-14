package com.survival2d.server.game.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Bullet extends MapObject implements Movable {

  Vector rawPosition;
  Vector direction;

  public void move() {
    moveBy(direction);
  }
}
