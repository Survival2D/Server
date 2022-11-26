package survival2d.config;

import com.tvd12.ezyfox.annotation.EzyProperty;
import com.tvd12.ezyfox.bean.annotation.EzyConfigurationBefore;

@EzyConfigurationBefore
public class LoginConfig {

  @EzyProperty("server.enableAuth")
  public static boolean isEnableAuth;
}
