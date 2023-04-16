package survival2d.service.impl;

import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import survival2d.match.entity.Match;
import survival2d.service.LobbyTeamService;
import survival2d.service.MatchingService;

@EzySingleton("matchingService")
@Slf4j
public class MatchingServiceImpl implements MatchingService {

  private final AtomicInteger currentMatchId = new AtomicInteger();
  private final Map<Integer, Match> matchIdToMatch = new ConcurrentHashMap<>();
  private final Map<String, Integer> playerIdToMatchId = new ConcurrentHashMap<>();
  @EzyAutoBind
  LobbyTeamService teamService;

  @Override
  public int createMatch(List<Integer> teamIds) {
    var matchId = currentMatchId.getAndIncrement();
    var match = new Match(matchId);
    matchIdToMatch.put(matchId, match);
    teamIds.stream()
        .map(teamId -> teamService.getTeam(teamId).get())
        .forEach(
            team -> {
              team.getPlayers()
                  .forEach(
                      playerId -> {
                        match.addPlayer(team.getId(), playerId);
                        playerIdToMatchId.put(playerId, matchId);
                      });
            });
    return matchId;
  }

  @Override
  public Optional<Integer> getMatchIdOfPlayer(String playerId) {
    if (playerIdToMatchId.containsKey(playerId)) {
      return Optional.of(playerIdToMatchId.get(playerId));
    }
    return Optional.empty();
  }

  @Override
  public Optional<Match> getMatchById(int matchId) {
    if (matchIdToMatch.containsKey(matchId)) {
      return Optional.of(matchIdToMatch.get(matchId));
    }
    return Optional.empty();
  }

  @Override
  public Optional<Match> getMatchOfPlayer(String playerId) {
    var optMatchId = getMatchIdOfPlayer(playerId);
    if (!optMatchId.isPresent()) {
      log.warn("matchId is not present");
      return Optional.empty();
    }
    var matchId = optMatchId.get();
    var optMatch = getMatchById(matchId);
    if (!optMatch.isPresent()) {
      log.warn("match is not present");
      return Optional.empty();
    }
    return optMatch;
  }

  @Override
  public void destroyMatch(int id) {
    matchIdToMatch.remove(id);
    playerIdToMatchId.entrySet().removeIf(entry -> entry.getValue() == id);
  }
}
