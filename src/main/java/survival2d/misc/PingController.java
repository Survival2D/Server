package survival2d.misc;

import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.core.annotation.EzyDoHandle;
import com.tvd12.ezyfox.core.annotation.EzyRequestController;
import com.tvd12.ezyfoxserver.entity.EzyUser;
import com.tvd12.ezyfoxserver.support.factory.EzyResponseFactory;
import survival2d.misc.request.PingByPlayerMoveRequest;
import survival2d.misc.response.PingByMatchInfoResponse;
import survival2d.misc.response.PingByPlayerMoveResponse;
import survival2d.misc.util.SamplePingData;

@EzyRequestController
public class PingController {

  @EzyAutoBind("pluginResponseFactory")
  private EzyResponseFactory responseFactory;

  @EzyDoHandle(MiscCommand.PING)
  public void handlePing(EzyUser user) {
    responseFactory.newObjectResponse().command(MiscCommand.PING).user(user).execute();
  }

  @EzyDoHandle(MiscCommand.PING_BY_PLAYER_MOVE)
  public void handlePingByPlayerMove(EzyUser user, PingByPlayerMoveRequest request) {
    responseFactory
        .newObjectResponse()
        .username(user.getName())
        .command(MiscCommand.PING_BY_PLAYER_MOVE)
        .data(
            PingByPlayerMoveResponse.builder()
                .username(SamplePingData.username)
                .position(SamplePingData.position)
                .rotation(SamplePingData.rotation)
                .build())
        .execute();
  }

  @EzyDoHandle(MiscCommand.PING_BY_MATCH_INFO)
  public void handlePingByMatchInfo(EzyUser user) {
    responseFactory
        .newObjectResponse()
        .username(user.getName())
        .command(MiscCommand.PING_BY_MATCH_INFO)
        .data(PingByMatchInfoResponse.builder().match(SamplePingData.match).build())
        .execute();
  }
}
