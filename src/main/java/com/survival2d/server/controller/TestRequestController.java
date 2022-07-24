package com.survival2d.server.controller;

import com.survival2d.server.request.TestRequest;
import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyfox.core.annotation.EzyRequestListener;
import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyfoxserver.context.EzyAppContext;
import com.tvd12.ezyfoxserver.entity.EzySession;
import com.tvd12.ezyfoxserver.entity.EzyUser;
import com.tvd12.ezyfoxserver.event.EzyUserSessionEvent;
import com.tvd12.ezyfoxserver.support.handler.EzyUserRequestHandler;
import lombok.Setter;

@Setter
@EzySingleton
//@EzyRequestListener("test")
public class TestRequestController extends EzyLoggable
    implements EzyUserRequestHandler<EzyAppContext, TestRequest> {

  protected EzyUser user;
  protected EzySession session;
  protected EzyAppContext appContext;

  @Override
  public void handle(
      EzyAppContext ezyAppContext,
      EzyUserSessionEvent ezyUserSessionEvent,
      TestRequest testRequest) {
    logger.info("TestRequestController::handle {}", testRequest);
  }
}
