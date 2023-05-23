package survival2d.match.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum WeaponType {
  HAND(AttackType.MELEE),
  PISTOL(AttackType.RANGE),
  SHOTGUN(AttackType.RANGE),
  AR(AttackType.RANGE);

  private final AttackType attackType;
}
