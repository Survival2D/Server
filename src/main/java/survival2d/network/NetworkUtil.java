package survival2d.network;

import io.netty.channel.Channel;
import java.nio.ByteBuffer;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import survival2d.data.ServerData;
import survival2d.network.json.response.BaseJsonResponse;

@Slf4j
public class NetworkUtil {

  public static void sendResponse(Channel channel, BaseJsonResponse response) {
    sendResponse(channel, response.toJson());
  }

  public static void sendResponse(Channel channel, String response) {
    channel.writeAndFlush(response);
  }

  public static void sendResponse(Collection<Integer> userIds, ByteBuffer byteBuffer) {
    for (int userId : userIds) {
      sendResponse(userId, byteBuffer);
    }
  }

  public static void sendResponse(Collection<Integer> userIds, byte[] bytes) {
    for (int userId : userIds) {
      sendResponse(userId, bytes);
    }
  }

  public static void sendResponse(int userId, ByteBuffer byteBuffer) {
    sendResponse(ServerData.getInstance().getUser(userId).getChannel(), byteBuffer);
  }

  public static void sendResponse(int userId, byte[] bytes) {
    sendResponse(ServerData.getInstance().getUser(userId).getChannel(), bytes);
  }

  public static void sendResponse(Channel channel, ByteBuffer byteBuffer) {
    channel.writeAndFlush(byteBuffer);
  }

  public static void sendResponse(Channel channel, byte[] bytes) {
    channel.writeAndFlush(bytes);
  }
}
