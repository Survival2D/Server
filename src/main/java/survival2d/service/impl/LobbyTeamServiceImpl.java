package survival2d.service.impl;

import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.val;
import survival2d.service.LobbyTeamService;
import survival2d.service.domain.LobbyTeam;

@EzySingleton
public class LobbyTeamServiceImpl implements LobbyTeamService {

  private static final AtomicInteger currentTeamId = new AtomicInteger();
  private final Map<Integer, LobbyTeam> teamIdToTeam = new HashMap<>();
  private final Map<String, Integer> usernameToTeam = new HashMap<>();

  @Override
  public int createTeam() {
    val teamId = currentTeamId.getAndIncrement();
    val team = new LobbyTeam(teamId);
    teamIdToTeam.put(teamId, team);
    return teamId;
  }

  @Override
  public Optional<LobbyTeam> getTeam(long teamId) {
    if (teamIdToTeam.containsKey(teamId)) {
      return Optional.of(teamIdToTeam.get(teamId));
    }
    return Optional.empty();
  }

  @Override
  public boolean joinTeam(String username, int teamId) {
    val optTeam = getTeam(teamId);
    if (!optTeam.isPresent()) {
      return false;
    }
    val team = optTeam.get();
    team.addPlayer(username);
    usernameToTeam.put(username, teamId);
    return true;
  }

  @Override
  public boolean quitTeam(String username, long teamId) {
    val optTeam = getTeam(teamId);
    if (!optTeam.isPresent()) {
      return false;
    }
    val team = optTeam.get();
    val result = team.removePlayer(username);
    if (result) {
      usernameToTeam.remove(username);
    }
    return result;
  }

  @Override
  public Optional<LobbyTeam> getTeamOfPlayer(String username) {
    if (usernameToTeam.containsKey(username)) {
      return getTeam(usernameToTeam.get(username));
    }
    return Optional.empty();
  }
}
