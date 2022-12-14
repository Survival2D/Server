package survival2d.service.entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import survival2d.match.constant.GameConstant;

public class LobbyTeam implements Team {

  @Getter private final int id;
  private final Set<String> playerUsernames = new HashSet<>(GameConstant.TEAM_PLAYER);

  public LobbyTeam(int teamId) {
    id = teamId;
  }

  @Override
  public void addPlayer(String username) {
    playerUsernames.add(username);
  }

  @Override
  public boolean removePlayer(String username) {
    return playerUsernames.remove(username);
  }

  @Override
  public Collection<String> getPlayers() {
    return new HashSet<>(playerUsernames);
  }
}
