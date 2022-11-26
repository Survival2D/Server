package survival2d.game.action;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlayerActionEnum {
  MOVE(PlayerMove.class),
  CHANGE_WEAPON(PlayerChangeWeapon.class),
  ATTACK(PlayerAttack.class);
  private final Class<? extends PlayerAction> actionClass;
}
