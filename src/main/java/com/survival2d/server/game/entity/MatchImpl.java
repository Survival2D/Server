package com.survival2d.server.game.entity;

import com.survival2d.server.game.entity.base.MapObject;
import java.util.Map;
import lombok.val;

public class MatchImpl implements Match {
  private Map<Long, MapObject> objects;
  private Map<Long, Team> teams;
  private long currentTick;

  @Override
  public void addPlayer(long teamId, String playerId) {
    val team = teams.computeIfAbsent(teamId, key -> new MatchTeam(teamId));
    team.addPlayer(playerId);
  }
}
