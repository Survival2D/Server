package survival2d.match.entity.team;

import survival2d.match.entity.player.Player;
import survival2d.service.entity.Team;
import java.util.Optional;

public interface MatchTeam extends Team {

  Optional<Player> getPlayer(String name);
}
