package survival2d.network;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import survival2d.flatbuffers.Request;

@Slf4j
public class FlatBuffersDecoder extends MessageToMessageDecoder<BinaryWebSocketFrame> {

  @Override
  protected void decode(
      ChannelHandlerContext channelHandlerContext,
      BinaryWebSocketFrame binaryWebSocketFrame,
      List<Object> list) {
    var byteBuf = binaryWebSocketFrame.content();
//    byteBuf.retain();

    var bytes = ByteBufUtil.getBytes(byteBuf);

    log.debug(Arrays.toString(bytes));

    var byteBuffer = ByteBuffer.wrap(bytes);

    var request = Request.getRootAsRequest(byteBuffer);

    list.add(request);
  }
}
