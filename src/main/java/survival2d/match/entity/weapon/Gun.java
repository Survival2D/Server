package survival2d.match.entity.weapon;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import survival2d.match.entity.Weapon;
import survival2d.match.entity.WeaponType;
import survival2d.match.entity.config.GunType;

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

//  public void shoot(Vector2 rawPosition, Vector2 direction) {
//    Bullet bullet = new Bullet(, rawPosition, direction, BulletType.NORMAL);
//  }
}
