package survival2d.match.entity;

import java.util.Optional;
import survival2d.service.entity.Team;

public interface MatchTeam extends Team {

  Optional<Player> getPlayer(String name);
}
