package com.survival2d.server.game.entity;

import com.survival2d.server.game.entity.math.Vector;
import lombok.Data;

@Data
public class PlayerImpl implements Player {

  String name;
  Vector position;
  PlayerState state;
  double rotation;
  double speed;
  Vector direction;

  public PlayerImpl(String playerId) {

  }
}
