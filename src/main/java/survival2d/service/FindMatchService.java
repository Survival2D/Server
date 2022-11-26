package survival2d.service;

import java.util.Optional;

public interface FindMatchService {

  Optional<Long> findMatch(long teamId);

  boolean cancelFindMatch(long teamId);
}
