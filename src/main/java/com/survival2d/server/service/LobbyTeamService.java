package com.survival2d.server.service;

import com.survival2d.server.service.domain.LobbyTeam;
import java.util.Optional;

public interface LobbyTeamService {

  long createTeam();

  Optional<LobbyTeam> getTeam(long teamId);

  boolean joinTeam(String username, long teamId);

  boolean quitTeam(String username, long teamId);

  Optional<LobbyTeam> getTeamOfPlayer(String username);
}
