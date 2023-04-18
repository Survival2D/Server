package survival2d.service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import survival2d.service.entity.LobbyTeam;

public class LobbyTeamService {
  @Getter(lazy = true)
  private static final LobbyTeamService instance = new LobbyTeamService();

  private final AtomicInteger currentTeamId = new AtomicInteger();
  private final Map<Integer, LobbyTeam> teamIdToTeam = new ConcurrentHashMap<>();
  private final Map<Integer, Integer> userIdToTeamId = new ConcurrentHashMap<>();

  public int createTeam() {
    var teamId = currentTeamId.getAndIncrement();
    var team = new LobbyTeam(teamId);
    teamIdToTeam.put(teamId, team);
    return teamId;
  }

  public Optional<LobbyTeam> getTeam(int teamId) {
    if (teamIdToTeam.containsKey(teamId)) {
      return Optional.of(teamIdToTeam.get(teamId));
    }
    return Optional.empty();
  }

  public boolean joinTeam(int userId, int teamId) {
    var optTeam = getTeam(teamId);
    if (optTeam.isEmpty()) {
      return false;
    }
    var team = optTeam.get();
    team.addMember(userId);
    userIdToTeamId.put(userId, teamId);
    return true;
  }

  public boolean quitTeam(int userId, int teamId) {
    var optTeam = getTeam(teamId);
    if (optTeam.isEmpty()) {
      return false;
    }
    var team = optTeam.get();
    var result = team.removeMember(userId);
    if (result) {
      userIdToTeamId.remove(userId);
    }
    return result;
  }

  public Optional<LobbyTeam> getTeamOfPlayer(int userId) {
    if (userIdToTeamId.containsKey(userId)) {
      return getTeam(userIdToTeamId.get(userId));
    }
    return Optional.empty();
  }
}
