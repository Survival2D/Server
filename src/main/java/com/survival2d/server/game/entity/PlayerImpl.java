package com.survival2d.server.game.entity;

import com.survival2d.server.game.entity.math.Vector;
import lombok.Data;

@Data
public class PlayerImpl implements Player {

  String playerId;
  Vector position;
  PlayerState state;
  double rotation;
  double speed;
  Vector direction;
  long team;

  public PlayerImpl(String playerId, long team) {
    this.playerId = playerId;
    this.team = team;
  }
}
