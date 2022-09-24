package com.survival2d.server.service.domain;

import java.util.Collection;

public interface Team {
  public Collection<String> getPlayers();

  public void addPlayer(String username);

  public boolean removePlayer(String username);

  //  public Optional<String> getCaptain();
}
