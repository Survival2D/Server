package survival2d.match.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GunType {
  PISTOL(BulletType.NORMAL, 20),
  SHOTGUN(BulletType.NORMAL, 20),
  SNIPER(BulletType.NORMAL, 20);
  private final BulletType bulletType;
  private final int bulletCapacity;
}
