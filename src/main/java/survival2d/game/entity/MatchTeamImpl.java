package survival2d.game.entity;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MatchTeamImpl implements MatchTeam {

  private final long id;

  private final Map<String, Player> players = new ConcurrentHashMap<>();

  public MatchTeamImpl(long teamId) {
    id = teamId;
  }

  @Override
  public Collection<String> getPlayers() {
    return null;
  }

  @Override
  public void addPlayer(String username) {
    //    players.computeIfAbsent(username, PlayerImpl::new);
  }

  @Override
  public boolean removePlayer(String username) {
    return players.remove(username) != null;
  }

  @Override
  public Optional<Player> getPlayer(String name) {
    if (players.containsKey(name)) {
      return Optional.of(players.get(name));
    }
    return Optional.empty();
  }
}
