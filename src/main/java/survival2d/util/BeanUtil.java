package survival2d.util;

import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.bean.annotation.EzyConfigurationAfter;
import com.tvd12.ezyfoxserver.support.factory.EzyResponseFactory;
import lombok.Getter;
import survival2d.service.MatchingService;

@EzyConfigurationAfter
public class BeanUtil {

  @EzyAutoBind @Getter private static MatchingService matchingService;

  @EzyAutoBind("pluginResponseFactory")
  @Getter
  private static EzyResponseFactory pluginResponseFactory;

  @EzyAutoBind("appResponseFactory")
  @Getter
  private static EzyResponseFactory appResponseFactory;
}
