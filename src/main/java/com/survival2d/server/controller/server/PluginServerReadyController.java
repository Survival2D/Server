package com.survival2d.server.controller.server;

import com.tvd12.ezyfox.core.annotation.EzyEventHandler;
import com.tvd12.ezyfoxserver.constant.EzyEventNames;
import com.tvd12.ezyfoxserver.context.EzyPluginContext;
import com.tvd12.ezyfoxserver.controller.EzyAbstractPluginEventController;
import com.tvd12.ezyfoxserver.event.EzyServerReadyEvent;

@EzyEventHandler(event = EzyEventNames.SERVER_READY)
public class PluginServerReadyController
    extends EzyAbstractPluginEventController<EzyServerReadyEvent> {

  @Override
  public void handle(EzyPluginContext ctx, EzyServerReadyEvent event) {
    logger.info("SURVIVAL2D PLUGIN - SERVER READY");
    logger.trace("SURVIVAL2D PLUGIN - SERVER READY");
  }

}
