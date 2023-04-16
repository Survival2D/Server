package survival2d.network;

import io.netty.channel.Channel;
import java.nio.ByteBuffer;
import survival2d.network.json.response.BaseJsonResponse;

public class NetworkUtil {

  public static void sendJsonResponse(Channel channel, BaseJsonResponse response) {
    sendTextResponse(channel, response.toJson());
  }

  public static void sendTextResponse(Channel channel, String response) {
    channel.writeAndFlush(response);
  }

  public static void sendBinaryResponse(Channel channel, ByteBuffer byteBuffer) {
    channel.writeAndFlush(byteBuffer);
  }

  public static void sendBinaryResponse(Channel channel, byte[] bytes) {
    channel.writeAndFlush(bytes);
  }
}
