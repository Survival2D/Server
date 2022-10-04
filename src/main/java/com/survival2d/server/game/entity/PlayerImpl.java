package com.survival2d.server.game.entity;

import java.util.concurrent.ThreadLocalRandom;
import lombok.Data;
import org.locationtech.jts.math.Vector2D;


@Data
public class PlayerImpl implements Player {

  String playerId;
  Vector2D position =
      new Vector2D(
          ThreadLocalRandom.current().nextDouble() * 100,
          ThreadLocalRandom.current().nextDouble() * 100);
  PlayerState state;
  double rotation;
  double speed = 10;
  Vector2D direction;
  long team;

  public PlayerImpl(String playerId, long team) {
    this.playerId = playerId;
    this.team = team;
  }
}
