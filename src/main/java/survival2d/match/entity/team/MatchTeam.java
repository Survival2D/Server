package survival2d.match.entity.team;

import java.util.Optional;

public interface MatchTeam extends Team {

  Optional<Player> getPlayer(String name);
}
