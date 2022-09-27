package com.survival2d.server.service.impl;

import com.survival2d.server.game.entity.Match;
import com.survival2d.server.game.entity.MatchImpl;
import com.survival2d.server.service.LobbyTeamService;
import com.survival2d.server.service.MatchingService;
import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import lombok.val;

@EzySingleton
public class MatchingServiceImpl implements MatchingService {
  @EzyAutoBind


  private final AtomicLong currentMatchId = new AtomicLong();
  private final ConcurrentHashMap<Long, Match> matchIdToMatch = new ConcurrentHashMap<>();
  @EzyAutoBind
  LobbyTeamService teamService;

  @Override
  public long createMatch(List<Long> teamIds) {
    val matchId = currentMatchId.getAndIncrement();
    val match = new MatchImpl(matchId);
    matchIdToMatch.put(matchId, match);
    teamIds.stream().map(teamId -> teamService.getTeam(teamId).get()).forEach(team -> {
      team.getPlayers().forEach(playerId -> match.addPlayer(team.getId(), playerId));
    });
    return matchId;
  }
}
