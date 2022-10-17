package com.survival2d.server.game.action;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PlayerChangeWeapon implements PlayerAction {

  private int weaponIndex;
}
