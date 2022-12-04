package survival2d.controller.client.misc;

import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.core.annotation.EzyDoHandle;
import com.tvd12.ezyfox.core.annotation.EzyRequestController;
import com.tvd12.ezyfoxserver.entity.EzyUser;
import com.tvd12.ezyfoxserver.support.factory.EzyResponseFactory;
import org.locationtech.jts.math.Vector2D;
import survival2d.controller.client.lobby.LobbyCommand;
import survival2d.controller.client.misc.response.PingByPlayerMoveResponse;
import survival2d.controller.client.misc.request.PingByPlayerMoveRequest;

@EzyRequestController
public class PingController {
  @EzyAutoBind("pluginResponseFactory")
  private EzyResponseFactory responseFactory;

  @EzyDoHandle(MiscCommand.PING_BY_PLAYER_MOVE)
  public void handlePlayerMove(EzyUser user, PingByPlayerMoveRequest request) {
    responseFactory
        .newObjectResponse()
        .username(user.getName())
        .command(MiscCommand.PING_BY_PLAYER_MOVE)
        .data(
            PingByPlayerMoveResponse.builder()
                .username(user.getName())
                .position(
                    new Vector2D(request.getDirection().getX(), request.getDirection().getY()))
                .rotation(request.getRotation())
                .build())
        .execute();
  }

  @EzyDoHandle(LobbyCommand.PING)
  public void ping(EzyUser user) {
    responseFactory.newObjectResponse().command(LobbyCommand.PING).user(user).execute();
  }
}
