package survival2d.match.entity;

import java.awt.Shape;
import java.util.Collection;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2;
import survival2d.match.action.PlayerAction;
import survival2d.match.entity.config.BulletType;


public interface Match {

  void addPlayer(int teamId, String playerId);

  Collection<String> getAllPlayers();

  void onReceivePlayerAction(String playerId, PlayerAction action);

  void onPlayerMove(String playerId, Vector2 direction, double rotation);

  void onPlayerAttack(String playerId, Vector2 direction);

  void createDamage(String playerId, Vector2 position, Shape shape, double damage);


  void makeDamage(String playerId, Vector2 position, Shape shape, double damage);

  void createBullet(String playerId, Vector2 position, Vector2 direction, BulletType type);

  void responseMatchInfo(String username);

  void responseMatchInfo();
}
