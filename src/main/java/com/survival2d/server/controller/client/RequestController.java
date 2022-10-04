package com.survival2d.server.controller.client;

import com.survival2d.server.service.PrizeService;
import com.survival2d.server.service.WheelService;
import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.core.annotation.EzyDoHandle;
import com.tvd12.ezyfox.core.annotation.EzyRequestController;
import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyfoxserver.entity.EzySession;
import com.tvd12.ezyfoxserver.entity.EzyUser;
import com.tvd12.ezyfoxserver.support.factory.EzyResponseFactory;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@EzyRequestController
public class RequestController extends EzyLoggable {

  private final WheelService wheelService;
  private final EzyResponseFactory responseFactory;

  @EzyAutoBind
  private PrizeService prizeService;

  @EzyDoHandle("spin")
  public void spin(EzySession session, EzyUser user) {
    int result = wheelService.spin();

    prizeService.createPrize(user.getName(), result);

    wheelService.decreaseQuantity(result);

    responseFactory
        .newObjectResponse()
        .command("spin")
        .param("result", result)
        .session(session)
        .execute();
  }

  //  @EzyDoHandle("test")
  //  public void test(TestRequest request, EzyUser user) {
  //    logger.info(
  //        "user {}, request: arr {}, obj {}",
  //        user.getName(),
  //        GameUtil.toGson(request.getArr()),
  //        GameUtil.toGson(request.getObj()));
  //    responseFactory.newObjectResponse().execute();
  //  }
}
