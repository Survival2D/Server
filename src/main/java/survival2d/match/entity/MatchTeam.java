package survival2d.match.entity;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MatchTeam {

  private final long id;

  private final Map<String, Player> players = new ConcurrentHashMap<>();

  public MatchTeam(long teamId) {
    id = teamId;
  }

  
  public Collection<String> getPlayers() {
    return null;
  }

  
  public void addPlayer(String username) {
    //    players.computeIfAbsent(username, PlayerImpl::new);
  }

  
  public boolean removePlayer(String username) {
    return players.remove(username) != null;
  }

  
  public Optional<Player> getPlayer(String name) {
    if (players.containsKey(name)) {
      return Optional.of(players.get(name));
    }
    return Optional.empty();
  }
}
