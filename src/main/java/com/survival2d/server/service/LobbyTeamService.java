package com.survival2d.server.service;

import com.survival2d.server.service.domain.LobbyTeam;
import java.util.Optional;

public interface LobbyTeamService {
  LobbyTeam createTeam();

  Optional<LobbyTeam> getTeam(long teamId);

  boolean joinTeam(long playerId, long teamId);
  boolean quitTeam(long playerId, long teamId);
}
