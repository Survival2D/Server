package survival2d.match.action;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlayerActionEnum {
  MOVE(ActionMove.class),
  CHANGE_WEAPON(ActionChangeWeapon.class),
  ATTACK(ActionAttack.class);
  private final Class<? extends PlayerAction> actionClass;
}
