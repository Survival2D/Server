package com.survival2d.server.service.domain;

import java.util.Collection;

public interface Team {
  Collection<String> getPlayers();

  void addPlayer(String username);

  boolean removePlayer(String username);

  //  public Optional<String> getCaptain();
}
