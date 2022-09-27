package com.survival2d.server.game.entity;

import com.survival2d.server.game.entity.base.MapObject;
import com.survival2d.server.game.entity.math.Vector;
import com.survival2d.server.network.match.GameCommand;
import com.survival2d.server.network.match.response.PlayerMoveResponse;
import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfoxserver.support.factory.EzyResponseFactory;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Getter
@Slf4j
public class MatchImpl implements Match {

  private final long id;
  private final Map<Long, MapObject> objects = new ConcurrentHashMap<>();
  private final Map<Long, MatchTeam> teams = new ConcurrentHashMap<>();
  private final Map<String, Long> playerIdToTeam = new ConcurrentHashMap<>();
  private final Map<String, Player> players = new ConcurrentHashMap<>();
  @EzyAutoBind
  private EzyResponseFactory appResponseFactory;
  private long currentTick;

  public MatchImpl(long id) {
    this.id = id;
  }

  @Override
  public void addPlayer(long teamId, String playerId) {
//    val team = teams.computeIfAbsent(teamId, key -> new MatchTeamImpl(teamId));
//    team.addPlayer(playerId);
//    playerIdToTeam.put(playerId, teamId);
    players.putIfAbsent(playerId, new PlayerImpl(playerId));
  }

  @Override
  public Collection<String> getAllPlayers() {
    return teams.values().stream().flatMap(team -> team.getPlayers().stream())
        .collect(Collectors.toList());
  }

  @Override
  public void onPlayerMove(String playerId, Vector direction, double rotation) {
    val player = players.get(playerId);
    if (player == null) {
      log.error("player {} is null", playerId);
      return;
    }
    val unitDirection = Vector.unit(direction);
    val moveBy = Vector.multiply(unitDirection, player.getSpeed());
    player.moveBy(moveBy);
    player.setRotation(rotation);
    appResponseFactory.newObjectResponse().command(GameCommand.PLAYER_MOVE)
        .data(PlayerMoveResponse.builder().username(player.getName()).position(player.getPosition())
            .rotation(player.getRotation()).build())
        .usernames(getAllPlayers())
        .execute();
  }
}
