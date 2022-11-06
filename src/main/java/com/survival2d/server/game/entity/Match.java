package com.survival2d.server.game.entity;

import com.survival2d.server.game.action.PlayerAction;
import com.survival2d.server.game.entity.config.BulletType;
import java.util.Collection;
import org.locationtech.jts.math.Vector2D;


public interface Match {

  void addPlayer(long teamId, String playerId);

  Collection<String> getAllPlayers();

  void onReceivePlayerAction(String playerId, PlayerAction action);

  void onPlayerMove(String playerId, Vector2D direction, double rotation);

  void onPlayerAttach(String playerId, Vector2D direction);

  void createDamage(String playerId, Vector2D position, double radius, double damage);


  void createBullet(String playerId, Vector2D position, Vector2D direction, BulletType type);
}
