package survival2d.network;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
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
    channel.writeAndFlush(new TextWebSocketFrame(response));
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
    if (ServerData.getInstance().getUser(userId) == null) {
      log.warn("User {} is not online", userId);
        return;
    }
    sendResponse(ServerData.getInstance().getUser(userId).getChannel(), byteBuffer);
  }

  public static void sendResponse(int userId, byte[] bytes) {
    sendResponse(ServerData.getInstance().getUser(userId).getChannel(), bytes);
  }

  public static void sendResponse(Channel channel, ByteBuffer byteBuffer) {
    channel.writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(byteBuffer)));
  }

  public static void sendResponse(Channel channel, byte[] bytes) {
    channel.writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(bytes)));
  }
}
