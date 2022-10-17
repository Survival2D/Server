package com.survival2d.server.game.action;

import lombok.Builder;
import lombok.Getter;
import org.locationtech.jts.math.Vector2D;

@Builder
@Getter
public class PlayerAttack implements PlayerAction {
  private Vector2D direction;
}
