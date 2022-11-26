package survival2d.game.entity;

import survival2d.service.domain.Team;
import java.util.Optional;

public interface MatchTeam extends Team {

  Optional<Player> getPlayer(String name);
}
