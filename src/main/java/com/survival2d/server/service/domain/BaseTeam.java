package com.survival2d.server.service.domain;

import com.survival2d.server.constant.GameConstant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;

public abstract class BaseTeam implements Team {
  @Getter private final long id;
  private final Set<String> playerUsernames = new HashSet<>(GameConstant.TEAM_PLAYER);

  protected BaseTeam(long teamId) {
    id = teamId;
  }

  @Override
  public void addPlayer(String username) {
    playerUsernames.add(username);
  }

  @Override
  public boolean removePlayer(String username) {
    return playerUsernames.remove(username);
  }

  @Override
  public Collection<String> getPlayers() {
    return new HashSet<>(playerUsernames);
  }
}
