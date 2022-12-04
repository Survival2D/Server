package survival2d.match.entity;

import survival2d.service.entity.Team;
import java.util.Optional;

public interface MatchTeam extends Team {

  Optional<Player> getPlayer(String name);
}
