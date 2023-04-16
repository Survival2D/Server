package survival2d.network.client;

import io.netty.channel.Channel;
import lombok.Data;

@Data
public class User {
  int id;
  String name;
  Channel channel;

  public User(int id, Channel channel) {
    this.id = id;
    this.channel = channel;
  }
}
