package survival2d.game.entity;

import survival2d.game.action.PlayerAction;
import survival2d.game.entity.base.Shape;
import survival2d.game.entity.config.BulletType;
import java.util.Collection;
import org.locationtech.jts.math.Vector2D;


public interface Match {

  void addPlayer(int teamId, String playerId);

  Collection<String> getAllPlayers();

  void onReceivePlayerAction(String playerId, PlayerAction action);

  void onPlayerMove(String playerId, Vector2D direction, double rotation);

  void onPlayerAttack(String playerId, Vector2D direction);

  void createDamage(String playerId, Vector2D position, Shape shape, double damage);


  void makeDamage(String playerId, Vector2D position, Shape shape, double damage);

  void createBullet(String playerId, Vector2D position, Vector2D direction, BulletType type);

  void responseMatchInfo(String username);
}
