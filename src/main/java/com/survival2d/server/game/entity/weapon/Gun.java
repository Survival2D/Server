package com.survival2d.server.game.entity.weapon;

import com.survival2d.server.game.entity.Weapon;
import com.survival2d.server.game.entity.WeaponType;
import com.survival2d.server.game.entity.config.GunType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Gun extends Weapon {
  private final WeaponType weaponType = WeaponType.GUN;

  private final GunType type;
  private int remainBullets;

  public int reload(int numBullet) {
    int numBulletCanLoad = type.getBulletCapacity() - remainBullets;
    int numBulletToLoad = Math.min(numBulletCanLoad, numBullet);
    this.remainBullets += numBulletToLoad;
    return numBullet - numBulletToLoad;
  }

//  public void shoot(Vector2D rawPosition, Vector2D direction) {
//    Bullet bullet = new Bullet(, rawPosition, direction, BulletType.NORMAL);
//  }
}
