package survival2d.common;

import com.tvd12.ezyfox.annotation.EzyProperty;

public class CommonConfig {
  @EzyProperty("server.testPing")
  public static boolean testPing;

  @EzyProperty("server.enableAuth")
  public static boolean isEnableAuth;
}
