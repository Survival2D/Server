package com.survival2d.server.network.lobby;

import com.survival2d.server.config.MapConfig;
import com.survival2d.server.network.lobby.response.GetConfigResponse;
import com.survival2d.server.util.EzyFoxUtil;
import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyfox.core.annotation.EzyDoHandle;
import com.tvd12.ezyfoxserver.entity.EzyUser;
import lombok.val;

@EzySingleton
public class GetConfigHandler {
  @EzyDoHandle(LobbyCommand.GET_CONFIG)
  public void handleGetConfig(EzyUser user) {
    val response = GetConfigResponse.builder().map(MapConfig.getInstance()).build();
    EzyFoxUtil.getResponseFactory()
        .newObjectResponse()
        .command(LobbyCommand.GET_CONFIG)
        .user(user)
        .data(response)
        .execute();
  }
}
