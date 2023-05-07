package survival2d.data;

import io.netty.channel.Channel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import survival2d.network.client.User;

public class ServerData {
  private final AtomicInteger currentUserId = new AtomicInteger();
  private final Map<String, Integer> channelMap = new ConcurrentHashMap<>();
  private final Map<Integer, User> userMap = new ConcurrentHashMap<>();

  public static ServerData getInstance() {
    return InstanceHolder.INSTANCE;
  }

  public int getUserId(Channel channel) {
    var channelId = channel.id().asLongText();
    var userId = channelMap.get(channelId);
    if (userId == null) {
      userId = newUserId();
      channelMap.put(channelId, userId);
    }
    return userId;
  }

  private int newUserId() {
    return currentUserId.incrementAndGet();
  }

  public User newUser(Channel channel) {
    var userId = getUserId(channel);
    var user = new User(userId, channel);
    userMap.put(user.getId(), user);
    return user;
  }

  public User getUser(int userId) {
    return userMap.get(userId);
  }

  public User getUser(Channel channel) {
    return getUser(getUserId(channel));
  }

  private static class InstanceHolder {
    private static final ServerData INSTANCE = new ServerData();
  }
}
