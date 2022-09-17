package com.survival2d.server.service;

import com.survival2d.server.service.domain.LobbyTeam;
import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.val;

public class LobbyTeamServiceImpl implements LobbyTeamService {
  private final Map<Long, LobbyTeam> teamIdToTeam = new HashMap<>();
  private final Map<Long, Long> playerIdToTeamId = new HashMap<>();
  @EzyAutoBind private MaxIdService maxIdService;

  @Override
  public LobbyTeam createTeam() {
    val teamId = maxIdService.incrementAndGet("lobbyTeam");
    val team = new LobbyTeam(teamId);
    teamIdToTeam.put(teamId, team);
    return team;
  }

  @Override
  public Optional<LobbyTeam> getTeam(long teamId) {
    if (teamIdToTeam.containsKey(teamId)) {
      return Optional.of(teamIdToTeam.get(teamId));
    }
    return Optional.empty();
  }

  @Override
  public boolean joinTeam(long playerId, long teamId) {
    val optTeam = getTeam(teamId);
    if (!optTeam.isPresent()) return false;
    val team = optTeam.get();
    team.addPlayer(playerId);
    return true;
  }

  @Override
  public boolean quitTeam(long playerId, long teamId) {
    val optTeam = getTeam(teamId);
    if (!optTeam.isPresent()) return false;
    val team = optTeam.get();
    return team.removePlayer(playerId);
  }
}
