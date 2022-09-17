package com.survival2d.server.service.domain;

import com.survival2d.server.constant.GameConstant;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public abstract class BaseTeam implements Team {
  private final long teamId;
  private final List<Long> players = new ArrayList<>(GameConstant.TEAM_PLAYER);

  protected BaseTeam(long teamId) {
    this.teamId = teamId;
  }

  @Override
  public void addPlayer(long playerId) {
    players.add(playerId);
  }

  @Override
  public boolean removePlayer(long playerId) {
    return players.remove(playerId);
  }
}
