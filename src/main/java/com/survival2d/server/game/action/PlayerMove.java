package com.survival2d.server.game.action;

import lombok.Builder;
import org.locationtech.jts.math.Vector2D;

@Builder
public class PlayerMove implements PlayerAction {
  private Vector2D direction;
  private double rotation;
}
