package com.survival2d.server.game.entity;

import com.survival2d.server.service.domain.BaseTeam;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MatchTeam extends BaseTeam {

  private final Map<String, Player> players = new ConcurrentHashMap<>();

  public MatchTeam(long teamId) {
    super(teamId);
  }

  @Override
  public void addPlayer(String username) {
    super.addPlayer(username);
    players.computeIfAbsent(username, PlayerImpl::new);
  }

  @Override
  public boolean removePlayer(String username) {
    players.remove(username);
    return super.removePlayer(username);
  }
}
