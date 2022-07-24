package com.survival2d.server.controller;

import com.survival2d.server.request.TestRequest;
import com.survival2d.server.util.GameUtil;
import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyfox.core.annotation.EzyRequestListener;
import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyfoxserver.context.EzyContext;
import com.tvd12.ezyfoxserver.event.EzyUserSessionEvent;
import com.tvd12.ezyfoxserver.support.handler.EzyUserRequestHandler;
import lombok.Setter;

@EzyRequestListener("test")
@EzySingleton
@Setter
public class TestRequestController extends EzyLoggable
    implements EzyUserRequestHandler<EzyContext, TestRequest> {

  @Override
  public void handle(
      EzyContext ezyAppContext, EzyUserSessionEvent ezyUserSessionEvent, TestRequest testRequest) {
    logger.info("handle request {}", GameUtil.toGson(testRequest));
  }
}
