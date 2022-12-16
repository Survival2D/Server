package survival2d.login;

import static com.tvd12.ezyfoxserver.constant.EzyEventNames.USER_REMOVED;

import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyfox.core.annotation.EzyEventHandler;
import com.tvd12.ezyfoxserver.context.EzyAppContext;
import com.tvd12.ezyfoxserver.controller.EzyAbstractAppEventController;
import com.tvd12.ezyfoxserver.event.EzyUserRemovedEvent;
import com.tvd12.ezyfoxserver.support.factory.EzyResponseFactory;

@EzySingleton
@EzyEventHandler(USER_REMOVED)
public class UserRemovedController extends EzyAbstractAppEventController<EzyUserRemovedEvent> {

  @EzyAutoBind private EzyResponseFactory responseFactory;

  @Override
  public void handle(EzyAppContext ctx, EzyUserRemovedEvent event) {
    logger.info("EzySmashers app: user {} removed", event.getUser());
    // TODO
  }
}
