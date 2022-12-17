package survival2d.util.ezyfox;

import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.bean.annotation.EzyConfigurationAfter;
import com.tvd12.ezyfoxserver.EzyZone;
import com.tvd12.ezyfoxserver.context.EzyZoneContext;
import com.tvd12.ezyfoxserver.entity.EzySession;
import com.tvd12.ezyfoxserver.entity.EzyUser;
import com.tvd12.ezyfoxserver.support.factory.EzyResponseFactory;
import com.tvd12.ezyfoxserver.wrapper.EzyZoneUserManager;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.val;
import survival2d.service.MatchingService;

@EzyConfigurationAfter
public class EzyFoxUtil {

  @EzyAutoBind @Getter private static MatchingService matchingService;

  @EzyAutoBind("pluginResponseFactory")
  @Getter
  private static EzyResponseFactory pluginResponseFactory;

  @EzyAutoBind("appResponseFactory")
  @Getter
  private static EzyResponseFactory appResponseFactory;

  @EzyAutoBind("zoneContext")
  @Getter
  private static EzyZoneContext zoneContext;

  public static EzyZone getZone() {
    return zoneContext.getZone();
  }

  public static EzyZoneUserManager getZoneUserManager() {
    return getZone().getUserManager();
  }

  public static EzyUser getUser(String username) {
    return getZoneUserManager().getUser(username);
  }

  public static EzySession getSession(String username) {
    val user = getUser(username);
    if (user == null) return null;
    return user.getSession();
  }

  public static List<EzySession> getSessions(Collection<String> usernames) {
    return usernames.stream()
        .map(EzyFoxUtil::getSession)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  public static void stream(byte[] data, String username) {
    val session = getSession(username);
    if (session == null) return;
    zoneContext.stream(data, session);
  }

  public static void stream(byte[] data, Collection<String> usernames) {
    val sessions = getSessions(usernames);
    zoneContext.stream(data, sessions);
  }
}
