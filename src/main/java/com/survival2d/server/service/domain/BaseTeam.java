package com.survival2d.server.service.domain;

import com.survival2d.server.constant.GameConstant;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

public abstract class BaseTeam implements Team {
  @Getter private final long id;
  private final List<String> playerUsernames = new ArrayList<>(GameConstant.TEAM_PLAYER);

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
  public List<String> getPlayers() {
    return new ArrayList<>(playerUsernames);
  }
}
