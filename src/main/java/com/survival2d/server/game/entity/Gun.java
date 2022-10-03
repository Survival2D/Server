package com.survival2d.server.game.entity;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class Gun extends Weapon {

  GunType type;
  int remainBullets;

  public int reload(int numBullet) {
    int numBulletCanLoad = type.getBulletCapacity() - remainBullets;
    int numBulletToLoad = Math.min(numBulletCanLoad, numBullet);
    this.remainBullets += numBulletToLoad;
    return numBullet - numBulletToLoad;
  }

  public void shoot(Vector2D rawPosition, Vector2D direction) {
    Bullet bullet = new Bullet(rawPosition, direction);
  }
}
