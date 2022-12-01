package survival2d.service;

import survival2d.game.entity.Match;
import java.util.List;
import java.util.Optional;

public interface MatchingService {

  int createMatch(List<Integer> teamIds);

  Optional<Integer> getMatchIdOfPlayer(String playerId);

  Optional<Match> getMatchById(int matchId);

  Optional<Match> getMatchOfPlayer(String playerId);

  void destroyMatch(int id);
}
