package com.survival2d.server.game.entity;

import com.survival2d.server.game.entity.math.Vector;
import java.util.Collection;

public interface Match {

  void addPlayer(long teamId, String playerId);

  Collection<String> getAllPlayers();

  void onPlayerMove(String playerId, Vector direction, double rotation);
}
