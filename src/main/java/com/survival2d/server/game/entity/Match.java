package com.survival2d.server.game.entity;

import java.util.Collection;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public interface Match {

  void addPlayer(long teamId, String playerId);

  Collection<String> getAllPlayers();

  void onPlayerMove(String playerId, Vector2D direction, double rotation);
}
