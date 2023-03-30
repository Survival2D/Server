package survival2d.network;

import com.miti99.netty.fbs.Packet;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebsocketDecoder extends MessageToMessageDecoder<BinaryWebSocketFrame> {

  @Override
  protected void decode(
      ChannelHandlerContext channelHandlerContext,
      BinaryWebSocketFrame binaryWebSocketFrame,
      List<Object> list) {
    var byteBuf = binaryWebSocketFrame.content();
    byteBuf.retain();

    var bytes = ByteBufUtil.getBytes(byteBuf);

    log.info(Arrays.toString(bytes));

    var byteBuffer = ByteBuffer.wrap(bytes);

    var packet = Packet.getRootAsPacket(byteBuffer);

    list.add(packet);
  }
}
