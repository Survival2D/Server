package survival2d.match.entity.weapon;

import survival2d.match.entity.config.WeaponType;
import survival2d.match.entity.config.GunType;
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
