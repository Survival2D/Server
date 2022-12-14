package survival2d.match.entity.team;

import java.util.Optional;
import survival2d.match.entity.player.Player;
import survival2d.service.entity.Team;

public interface MatchTeam extends Team {

  Optional<Player> getPlayer(String name);
}
