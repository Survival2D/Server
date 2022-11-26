package survival2d.network.lobby;

import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.core.annotation.EzyDoHandle;
import com.tvd12.ezyfox.core.annotation.EzyRequestController;
import com.tvd12.ezyfoxserver.entity.EzyUser;
import com.tvd12.ezyfoxserver.support.factory.EzyResponseFactory;
import org.locationtech.jts.math.Vector2D;
import survival2d.network.lobby.request.PingByPlayerMoveRequest;
import survival2d.network.lobby.response.PingByPlayerMoveResponse;
import survival2d.network.match.MatchCommand;

@EzyRequestController
public class PingByPlayerMoveController {
  @EzyAutoBind private EzyResponseFactory responseFactory;

  @EzyDoHandle(MatchCommand.PING_BY_PLAYER_MOVE)
  public void handlePlayerMove(EzyUser user, PingByPlayerMoveRequest request) {
    responseFactory
        .newObjectResponse()
        .username(user.getName())
        .command(MatchCommand.PING_BY_PLAYER_MOVE)
        .data(
            PingByPlayerMoveResponse.builder()
                .username(user.getName())
                .position(
                    new Vector2D(request.getDirection().getX(), request.getDirection().getY()))
                .rotation(request.getRotation())
                .build())
        .execute();
  }
}
