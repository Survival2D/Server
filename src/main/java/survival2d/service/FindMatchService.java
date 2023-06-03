package survival2d.service;

import com.google.common.collect.Lists;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import survival2d.match.config.GameConfig;

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
    if (matchingTeams.size() < GameConfig.getInstance().getNumTeamsPerMatch()) {
      return Optional.empty();
    }
    var teams = matchingTeams.stream().toList();
    matchingTeams.clear();
    log.info("Match teams {} together", teams);
    var matchId = MatchingService.getInstance().createMatch(Lists.newArrayList(teams));
    return Optional.of(matchId);
  }

  @Synchronized
  public boolean cancelFindMatch(int teamId) {
    return matchingTeams.remove(teamId);
  }
}
