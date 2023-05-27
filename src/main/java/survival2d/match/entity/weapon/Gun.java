package survival2d.match.entity.weapon;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import survival2d.flatbuffers.GunTypeEnum;
import survival2d.match.config.GameConfig;
import survival2d.match.config.GunConfig;
import survival2d.match.type.GunType;
import survival2d.match.type.WeaponType;

@RequiredArgsConstructor
@Getter
public class Gun extends Weapon {
  private final WeaponType weaponType = WeaponType.GUN;

  private final GunType type;
  private int remainBullets = 0;

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

  public final byte getFbsGun() {
    return switch (type) {
      case PISTOL -> GunTypeEnum.PISTOL;
      case SHOTGUN -> GunTypeEnum.SHOTGUN;
      case SNIPER -> GunTypeEnum.SNIPER;
    };
  }
}
