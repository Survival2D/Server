package survival2d.match.entity.team;

import java.util.Collection;

public interface Team {

  void addPlayer(String playerId);

  Collection<String> getPlayers();
}
