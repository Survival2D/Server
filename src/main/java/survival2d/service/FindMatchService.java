package survival2d.service;

import java.util.Optional;

public interface FindMatchService {

  Optional<Integer> findMatch(int teamId);

  boolean cancelFindMatch(int teamId);
}
