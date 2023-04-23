package survival2d.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import survival2d.match.entity.match.Match;

@Slf4j
public class MatchingService {
  @Getter(lazy = true)
  private static final MatchingService instance = new MatchingService();

  private final AtomicInteger currentMatchId = new AtomicInteger();
  private final Map<Integer, Match> matchIdToMatch = new ConcurrentHashMap<>();
  private final Map<Integer, Integer> userIdToMatchId = new ConcurrentHashMap<>();

  public int createMatch(List<Integer> teamIds) {
    var matchId = currentMatchId.getAndIncrement();
    var match = new Match(matchId);
    matchIdToMatch.put(matchId, match);
    teamIds.stream()
        .map(teamId -> LobbyTeamService.getInstance().getTeam(teamId).get())
        .forEach(
            team -> {
              team.getMemberIds()
                  .forEach(
                      userId -> {
                        match.addPlayer(team.getId(), userId);
                        userIdToMatchId.put(userId, matchId);
                      });
            });
    return matchId;
  }

  public Optional<Integer> getMatchIdOfUser(int userId) {
    if (userIdToMatchId.containsKey(userId)) {
      return Optional.of(userIdToMatchId.get(userId));
    }
    return Optional.empty();
  }

  public Optional<Match> getMatchById(int matchId) {
    if (matchIdToMatch.containsKey(matchId)) {
      return Optional.of(matchIdToMatch.get(matchId));
    }
    return Optional.empty();
  }

  public Optional<Match> getMatchOfUser(int userId) {
    var optMatchId = getMatchIdOfUser(userId);
    if (optMatchId.isEmpty()) {
      log.warn("matchId is not present");
      return Optional.empty();
    }
    var matchId = optMatchId.get();
    var optMatch = getMatchById(matchId);
    if (optMatch.isEmpty()) {
      log.warn("match is not present");
      return Optional.empty();
    }
    return optMatch;
  }

  public void destroyMatch(int matchId) {
    matchIdToMatch.remove(matchId);
    userIdToMatchId.entrySet().removeIf(entry -> entry.getValue() == matchId);
  }
}
