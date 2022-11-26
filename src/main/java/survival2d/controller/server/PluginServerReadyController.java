package survival2d.controller.server;

import static com.tvd12.ezyfoxserver.constant.EzyEventNames.SERVER_READY;

import com.tvd12.ezyfox.core.annotation.EzyEventHandler;
import com.tvd12.ezyfoxserver.context.EzyPluginContext;
import com.tvd12.ezyfoxserver.controller.EzyAbstractPluginEventController;
import com.tvd12.ezyfoxserver.event.EzyServerReadyEvent;

@EzyEventHandler(SERVER_READY)
public class PluginServerReadyController
    extends EzyAbstractPluginEventController<EzyServerReadyEvent> {

  @Override
  public void handle(EzyPluginContext ctx, EzyServerReadyEvent event) {
    logger.info("SURVIVAL2D PLUGIN - SERVER READY");
    logger.trace("SURVIVAL2D PLUGIN - SERVER READY");
  }
}
