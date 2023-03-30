package survival2d.network;

import com.miti99.netty.json.response.BaseJsonResponse;
import io.netty.channel.Channel;
import java.nio.ByteBuffer;

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
