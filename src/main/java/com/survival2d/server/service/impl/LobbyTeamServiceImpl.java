package com.survival2d.server.service.impl;

import com.survival2d.server.service.LobbyTeamService;
import com.survival2d.server.service.domain.LobbyTeam;
import com.tvd12.ezyfox.annotation.EzyAutoImpl;
import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import lombok.val;

@EzySingleton
public class LobbyTeamServiceImpl implements LobbyTeamService {
  private final Map<Long, LobbyTeam> teamIdToTeam = new HashMap<>();
  private static final AtomicLong currentTeamId = new AtomicLong();
  private final Map<String, Long> usernameToTeam = new HashMap<>();

  @Override
  public long createTeam() {
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
  public boolean joinTeam(String username, long teamId) {
    val optTeam = getTeam(teamId);
    if (!optTeam.isPresent()) return false;
    val team = optTeam.get();
    team.addPlayer(username);
    usernameToTeam.put(username, teamId);
    return true;
  }

  @Override
  public boolean quitTeam(String username, long teamId) {
    val optTeam = getTeam(teamId);
    if (!optTeam.isPresent()) return false;
    val team = optTeam.get();
    val result = team.removePlayer(username);
    if (result) usernameToTeam.remove(username);
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
