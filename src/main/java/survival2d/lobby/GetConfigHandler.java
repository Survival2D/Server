package survival2d.lobby;

import survival2d.match.config.MapConfig;
import survival2d.controller.client.lobby.LobbyCommand;
import survival2d.controller.client.lobby.response.GetConfigResponse;
import survival2d.util.EzyFoxUtil;
import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyfox.core.annotation.EzyDoHandle;
import com.tvd12.ezyfoxserver.entity.EzyUser;
import lombok.val;

@EzySingleton
public class GetConfigHandler {
  @EzyDoHandle(LobbyCommand.GET_CONFIG)
  public void handleGetConfig(EzyUser user) {
    val response = GetConfigResponse.builder().map(MapConfig.getInstance()).build();
    EzyFoxUtil.getInstance().getResponseFactory()
        .newObjectResponse()
        .command(LobbyCommand.GET_CONFIG)
        .user(user)
        .data(response)
        .execute();
  }
}
