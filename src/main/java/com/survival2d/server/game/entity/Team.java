package com.survival2d.server.game.entity;

import java.util.Collection;

public interface Team {

  void addPlayer(String playerId);
  Collection<String> getPlayers();
}
