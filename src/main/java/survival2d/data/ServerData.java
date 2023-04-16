package survival2d.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import survival2d.network.client.User;

@Getter
public class ServerData {
  private final AtomicInteger currentUserId = new AtomicInteger();
  private final Map<String, Integer> channelMap = new ConcurrentHashMap<>();
  private final Map<Integer, User> userMap = new ConcurrentHashMap<>();

  public static ServerData getInstance() {
    return InstanceHolder.INSTANCE;
  }

  public int newUserId() {
    return currentUserId.incrementAndGet();
  }

  private static class InstanceHolder {
    private static final ServerData INSTANCE = new ServerData();
  }
}
