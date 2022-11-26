package survival2d.game.action;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class PlayerChangeWeapon implements PlayerAction {

  private int weaponIndex;
}
