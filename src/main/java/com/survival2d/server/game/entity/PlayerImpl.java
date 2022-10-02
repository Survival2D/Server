package com.survival2d.server.game.entity;

import com.survival2d.server.game.entity.math.Vector;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Data;
import org.apache.commons.lang3.RandomUtils;

@Data
public class PlayerImpl implements Player {

  String playerId;
  Vector position = new Vector(ThreadLocalRandom.current().nextDouble() * 100, ThreadLocalRandom.current().nextDouble() * 100);
  PlayerState state;
  double rotation;
  double speed = 10;
  Vector direction;
  long team;

  public PlayerImpl(String playerId, long team) {
    this.playerId = playerId;
    this.team = team;
  }
}
