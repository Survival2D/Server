package survival2d.network;

import io.netty.channel.Channel;
import java.nio.ByteBuffer;
import lombok.extern.slf4j.Slf4j;
import survival2d.data.ServerData;
import survival2d.network.client.User;
import survival2d.network.json.response.BaseJsonResponse;

@Slf4j
public class NetworkUtil {
  public static User getUserById(int userId) {
    if (ServerData.getInstance().getUserMap().get(userId) == null) {
      log.error("getUserById - user {} is null", userId);
    }
    return ServerData.getInstance().getUserMap().get(userId);
  }

  public static void sendJsonResponse(Channel channel, BaseJsonResponse response) {
    sendTextResponse(channel, response.toJson());
  }

  public static void sendTextResponse(Channel channel, String response) {
    channel.writeAndFlush(response);
  }

  public static void sendBinaryResponse(int userId, ByteBuffer byteBuffer) {
    sendBinaryResponse(getUserById(userId).getChannel(), byteBuffer);
  }

  public static void sendBinaryResponse(int userId, byte[] bytes) {
    sendBinaryResponse(getUserById(userId).getChannel(), bytes);
  }

  public static void sendBinaryResponse(Channel channel, ByteBuffer byteBuffer) {
    channel.writeAndFlush(byteBuffer);
  }

  public static void sendBinaryResponse(Channel channel, byte[] bytes) {
    channel.writeAndFlush(bytes);
  }
}
