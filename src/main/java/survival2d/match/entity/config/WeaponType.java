package survival2d.match.entity.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum WeaponType {
  HAND(AttachType.MELEE),
  GUN(AttachType.RANGE);

  private final AttachType attachType;
}
