package survival2d.service;

import java.util.Optional;
import survival2d.service.entity.LobbyTeam;

public interface LobbyTeamService {

  int createTeam();

  Optional<LobbyTeam> getTeam(int teamId);

  boolean joinTeam(String username, int teamId);

  boolean quitTeam(String username, int teamId);

  Optional<LobbyTeam> getTeamOfPlayer(String username);
}
