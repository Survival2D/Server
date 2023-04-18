package survival2d.match.action;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class ActionChangeWeapon implements PlayerAction {

  private int weaponIndex;
}
