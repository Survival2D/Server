package survival2d.ping.controller;

import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.core.annotation.EzyDoHandle;
import com.tvd12.ezyfox.core.annotation.EzyRequestController;
import com.tvd12.ezyfoxserver.entity.EzyUser;
import com.tvd12.ezyfoxserver.support.factory.EzyResponseFactory;
import survival2d.ping.cmd.PingCommand;
import survival2d.ping.request.PingByPlayerMoveRequest;
import survival2d.ping.response.PingByMatchInfoResponse;
import survival2d.ping.response.PingByPlayerMoveResponse;
import survival2d.ping.data.SamplePingData;

@EzyRequestController
public class PingController {

  @EzyAutoBind("pluginResponseFactory")
  private EzyResponseFactory responseFactory;

  @EzyDoHandle(PingCommand.PING)
  public void handlePing(EzyUser user) {
    responseFactory.newObjectResponse().command(PingCommand.PING).user(user).execute();
  }

  @EzyDoHandle(PingCommand.PING_BY_PLAYER_MOVE)
  public void handlePingByPlayerMove(EzyUser user, PingByPlayerMoveRequest request) {
    responseFactory
        .newObjectResponse()
        .username(user.getName())
        .command(PingCommand.PING_BY_PLAYER_MOVE)
        .data(
            PingByPlayerMoveResponse.builder()
                .username(SamplePingData.userId)
                .position(SamplePingData.position)
                .rotation(SamplePingData.rotation)
                .build())
        .execute();
  }

  @EzyDoHandle(PingCommand.PING_BY_MATCH_INFO)
  public void handlePingByMatchInfo(EzyUser user) {
    responseFactory
        .newObjectResponse()
        .username(user.getName())
        .command(PingCommand.PING_BY_MATCH_INFO)
        .data(PingByMatchInfoResponse.builder().match(SamplePingData.match).build())
        .execute();
  }
}
