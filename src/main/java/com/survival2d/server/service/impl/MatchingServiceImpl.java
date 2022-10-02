package com.survival2d.server.service.impl;

import com.survival2d.server.game.entity.Match;
import com.survival2d.server.game.entity.MatchImpl;
import com.survival2d.server.service.LobbyTeamService;
import com.survival2d.server.service.MatchingService;
import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyfoxserver.support.factory.EzyResponseFactory;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import lombok.val;

@EzySingleton
public class MatchingServiceImpl implements MatchingService {

  @EzyAutoBind

  private final AtomicLong currentMatchId = new AtomicLong();
  private final Map<Long, Match> matchIdToMatch = new ConcurrentHashMap<>();
  private final Map<String, Long> playerIdToMatchId = new ConcurrentHashMap<>();
  @EzyAutoBind
  LobbyTeamService teamService;
  @EzyAutoBind
  private EzyResponseFactory responseFactory;

  @Override
  public long createMatch(List<Long> teamIds) {
    val matchId = currentMatchId.getAndIncrement();
    val match = new MatchImpl(matchId, responseFactory);
    matchIdToMatch.put(matchId, match);
    teamIds.stream().map(teamId -> teamService.getTeam(teamId).get()).forEach(team -> {
      team.getPlayers().forEach(playerId -> {
        match.addPlayer(team.getId(), playerId);
        playerIdToMatchId.put(playerId, matchId);
      });
    });
    return matchId;
  }

  @Override
  public Optional<Long> getMatchIdOfPlayer(String playerId) {
    if (playerIdToMatchId.containsKey(playerId)) {
      return Optional.of(playerIdToMatchId.get(playerId));
    }
    return Optional.empty();
  }

  @Override
  public Optional<Match> getMatchById(long matchId) {
    if (matchIdToMatch.containsKey(matchId)) {
      return Optional.of(matchIdToMatch.get(matchId));
    }
    return Optional.empty();
  }
}
