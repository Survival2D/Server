package survival2d.network.client;

import io.netty.channel.Channel;
import lombok.Data;
import survival2d.ServerConstant;

@Data
public class User {
  int id;
  String name;
  Channel channel;

  public User(int id, Channel channel) {
    this.id = id;
    this.channel = channel;
    this.name = ServerConstant.DEFAULT_USER_NAME_PREFIX + id;
  }
}
