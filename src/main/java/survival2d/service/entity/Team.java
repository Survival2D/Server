package survival2d.service.entity;

import java.util.Collection;

public interface Team {

  Collection<String> getPlayers();

  void addPlayer(String username);

  boolean removePlayer(String username);
}
