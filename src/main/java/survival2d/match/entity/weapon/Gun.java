package survival2d.match.entity.weapon;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import survival2d.match.entity.config.GunType;
import survival2d.match.entity.config.WeaponType;

@RequiredArgsConstructor
@Getter
public class Gun extends Weapon {
  private final WeaponType weaponType = WeaponType.GUN;

  private final GunType type;
  private int remainBullets = 10;

  public int reload(int numBullet) {
    int numBulletCanLoad = type.getBulletCapacity() - remainBullets;
    int numBulletToLoad = Math.min(numBulletCanLoad, numBullet);
    remainBullets += numBulletToLoad;
    return numBullet - numBulletToLoad;
  }

  public boolean isReadyToShoot() {
    return true;//FIXME
//    return remainBullets > 0;
  }

  //  public void shoot(Vector2D rawPosition, Vector2D direction) {
  //    Bullet bullet = new Bullet(, rawPosition, direction, BulletType.NORMAL);
  //  }
}
