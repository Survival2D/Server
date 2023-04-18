package survival2d.service;

import com.google.common.collect.Lists;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FindMatchService {
  @Getter(lazy = true)
  private static final FindMatchService instance = new FindMatchService();

  private final Set<Integer> matchingTeams = ConcurrentHashMap.newKeySet();

  @Synchronized
  public Optional<Integer> findMatch(int teamId) {
    if (matchingTeams.contains(teamId)) {
      log.warn("Team {} is already in matchingTeams", teamId);
    }
    matchingTeams.add(teamId);
    if (matchingTeams.size() < 2) {
      return Optional.empty();
    }
    var optTeam = matchingTeams.stream().filter(id -> id != teamId).findFirst();
    if (optTeam.isEmpty()) {
      return Optional.empty();
    }
    log.info("Match teams {} and {} together", teamId, optTeam.get());
    matchingTeams.remove(teamId);
    matchingTeams.remove(optTeam.get());
    var matchId =
        MatchingService.getInstance().createMatch(Lists.newArrayList(teamId, optTeam.get()));
    return Optional.of(matchId);
  }

  @Synchronized
  public boolean cancelFindMatch(int teamId) {
    return matchingTeams.remove(teamId);
  }
}
