package com.survival2d.server.game.action;

import lombok.Builder;
import org.locationtech.jts.math.Vector2D;

@Builder
public class PlayerAttack implements PlayerAction {
  private Vector2D direction;
}
