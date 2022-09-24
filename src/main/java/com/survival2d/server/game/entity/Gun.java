package com.survival2d.server.game.entity;

import com.survival2d.server.game.entity.math.Vector;

public class Gun extends Weapon {

  GunType type;
  int remainBullets;

  public int reload(int numBullet) {
    int numBulletCanLoad = type.getBulletCapacity() - remainBullets;
    int numBulletToLoad = Math.min(numBulletCanLoad, numBullet);
    this.remainBullets += numBulletToLoad;
    return numBullet - numBulletToLoad;
  }

  public void shoot(Vector rawPosition, Vector direction) {
    Bullet bullet = new Bullet(rawPosition, direction);
  }
}
