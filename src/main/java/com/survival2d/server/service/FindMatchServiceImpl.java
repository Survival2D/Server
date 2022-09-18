package com.survival2d.server.service;

import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
@EzySingleton
public class FindMatchServiceImpl implements FindMatchService {
  private final Set<Long> matchingTeams = new HashSet<>();
  @EzyAutoBind MatchingService matchingService;

  @Override
  @Synchronized
  public Optional<Long> findMatch(long teamId) {
    if (matchingTeams.contains(teamId)) {
      log.warn("Team {} is already in matchingTeams", teamId);
    }
    if (matchingTeams.isEmpty()) {
      return Optional.empty();
    }
    val optTeam = matchingTeams.stream().filter(id -> id != teamId).findFirst();
    if (!optTeam.isPresent()) {
      return Optional.empty();
    }
    matchingTeams.remove(teamId);
    matchingTeams.remove(optTeam.get());
    val matchId = matchingService.newMatch();
    return Optional.of(matchId);
  }

  @Override
  @Synchronized
  public boolean cancelFindMatch(long teamId) {
    return matchingTeams.remove(teamId);
  }
}
