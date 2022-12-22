package survival2d.match.entity.match;

import java.util.Collection;
import java.util.List;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import survival2d.match.action.PlayerAction;
import survival2d.match.entity.base.MapObject;
import survival2d.match.entity.base.Shape;
import survival2d.match.entity.config.BulletType;
import survival2d.match.entity.item.ItemOnMap;
import survival2d.match.entity.obstacle.Container;
import survival2d.match.entity.player.Player;

public interface Match {

  void addPlayer(int teamId, String playerId);

  Collection<String> getAllUsernames();

  void onReceivePlayerAction(String playerId, PlayerAction action);

  void onPlayerMove(String playerId, Vector2D direction, double rotation);

  void onPlayerAttack(String playerId, Vector2D direction);

  void createDamage(String playerId, Vector2D position, Shape shape, double damage);

  void makeDamage(String playerId, Vector2D position, Shape shape, double damage);

  void createBullet(String playerId, Vector2D position, Vector2D direction, BulletType type);

  void responseMatchInfo(String username);

  void responseMatchInfo();

  // for bots

  Player getPlayerInfo(String username);

  MapObject getObjectsById(int id);

  Collection<Player> getNearByPlayer(Vector2D position);

  Collection<Container> getNearByContainer(Vector2D position);

  Collection<ItemOnMap> getNearByItem(Vector2D position);

  List<Vector2D> getPathFromTo(Vector2D from, Vector2D to);

  void setPlayerAutoPlay(String username, boolean enable);
}
