package com.survival2d.server.service.domain;

import java.util.List;

public interface Team {
  public List<Long> getPlayers();

  public void addPlayer(long playerId);

  public boolean removePlayer(long playerId);

  //  public Optional<String> getCaptain();
}
