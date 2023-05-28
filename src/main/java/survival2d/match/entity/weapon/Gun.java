package survival2d.match.entity.weapon;

import lombok.Getter;
import survival2d.match.config.GameConfig;
import survival2d.match.config.GunConfig;
import survival2d.match.type.GunType;
import survival2d.match.type.WeaponType;

@Getter
public class Gun extends Weapon {
  private final WeaponType weaponType = WeaponType.GUN;

  private final GunType type;
  private int remainBullets;

  public Gun(GunType type) {
    this.type = type;
    this.remainBullets = getConfig().getCapacity();
  }

  public int reload(int numBullet) {
    int maxBulletCanLoad = getConfig().getCapacity() - remainBullets;
    int numBulletToLoad = Math.min(maxBulletCanLoad, numBullet);
    remainBullets += numBulletToLoad;
    return numBulletToLoad;
  }

  public boolean isReadyToShoot() {
    return remainBullets > 0;
  }

  @Override
  public GunConfig getConfig() {
    return GameConfig.getInstance().getGunConfigs().get(type);
  }

  public final byte getFbsGunType() {
    return type.toFbsGunType();
  }
}
