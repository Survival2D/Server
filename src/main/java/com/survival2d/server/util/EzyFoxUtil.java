package com.survival2d.server.util;


import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfoxserver.support.factory.EzyResponseFactory;
import lombok.Getter;

public class EzyFoxUtil {

  @Getter
  @EzyAutoBind
  private static EzyResponseFactory responseFactory;
}
