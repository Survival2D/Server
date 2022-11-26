package survival2d.game.entity;

import java.util.Collection;

public interface Team {

  void addPlayer(String playerId);

  Collection<String> getPlayers();
}
