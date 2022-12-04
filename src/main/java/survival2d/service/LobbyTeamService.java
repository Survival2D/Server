package survival2d.service;

import survival2d.service.entity.LobbyTeam;
import java.util.Optional;

public interface LobbyTeamService {

  int createTeam();

  Optional<LobbyTeam> getTeam(int teamId);

  boolean joinTeam(String username, int teamId);

  boolean quitTeam(String username, int teamId);

  Optional<LobbyTeam> getTeamOfPlayer(String username);
}
