package com.survival2d.server.game.entity;

import com.survival2d.server.game.entity.base.MapObject;
import com.survival2d.server.network.match.MatchCommand;
import com.survival2d.server.network.match.response.PlayerMoveResponse;
import com.survival2d.server.util.vector.VectorUtil;
import com.tvd12.ezyfoxserver.support.factory.EzyResponseFactory;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.locationtech.jts.math.Vector2D;


@Getter
@Slf4j
public class MatchImpl implements Match {

  private final long id;
  private final Map<Long, MapObject> objects = new ConcurrentHashMap<>();
  private final Map<Long, MatchTeam> teams = new ConcurrentHashMap<>();
  private final Map<String, Long> playerIdToTeam = new ConcurrentHashMap<>();
  private final Map<String, Player> players = new ConcurrentHashMap<>();
  private final transient EzyResponseFactory responseFactory;
  private long currentTick;

  public MatchImpl(long id, EzyResponseFactory responseFactory) {
    this.id = id;
    this.responseFactory = responseFactory;
  }

  @Override
  public void addPlayer(long teamId, String playerId) {
    //    val team = teams.computeIfAbsent(teamId, key -> new MatchTeamImpl(teamId));
    //    team.addPlayer(playerId);
    //    playerIdToTeam.put(playerId, teamId);
    players.putIfAbsent(playerId, new PlayerImpl(playerId, teamId));
  }

  @Override
  public Collection<String> getAllPlayers() {
    return players.keySet();
  }

  @Override
  public void onPlayerMove(String playerId, Vector2D direction, double rotation) {
    val player = players.get(playerId);
    if (player == null) {
      log.error("player {} is null", playerId);
      return;
    }
    if (!VectorUtil.isZero(direction)) {
      val unitDirection = direction.normalize();
      val moveBy = unitDirection.multiply(player.getSpeed());
      player.moveBy(moveBy);
    }
    player.setRotation(rotation);
    responseFactory
        .newObjectResponse()
        .command(MatchCommand.PLAYER_MOVE)
        .data(
            PlayerMoveResponse.builder()
                .username(player.getPlayerId())
                .position(player.getPosition())
                .rotation(player.getRotation())
                .build())
        .usernames(getAllPlayers())
        .execute();
  }
}
