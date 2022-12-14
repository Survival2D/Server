package survival2d.service.impl;

import com.google.common.collect.Lists;
import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import survival2d.service.FindMatchService;
import survival2d.service.MatchingService;

@Slf4j
@EzySingleton
public class FindMatchServiceImpl implements FindMatchService {

  private final Set<Integer> matchingTeams = new HashSet<>();
  @EzyAutoBind MatchingService matchingService;

  @Override
  @Synchronized
  public Optional<Integer> findMatch(int teamId) {
    if (matchingTeams.contains(teamId)) {
      log.warn("Team {} is already in matchingTeams", teamId);
    }
    matchingTeams.add(teamId);
    if (matchingTeams.size() < 2) {
      return Optional.empty();
    }
    val optTeam = matchingTeams.stream().filter(id -> id != teamId).findFirst();
    if (!optTeam.isPresent()) {
      return Optional.empty();
    }
    log.info("Match teams {} and {} together", teamId, optTeam.get());
    matchingTeams.remove(teamId);
    matchingTeams.remove(optTeam.get());
    val matchId = matchingService.createMatch(Lists.newArrayList(teamId, optTeam.get()));
    return Optional.of(matchId);
  }

  @Override
  @Synchronized
  public boolean cancelFindMatch(int teamId) {
    return matchingTeams.remove(teamId);
  }
}
