package com.survival2d.server.game.entity;

import com.survival2d.server.service.domain.Team;
import java.util.Optional;

public interface MatchTeam extends Team {

  Optional<Player> getPlayer(String name);

}
