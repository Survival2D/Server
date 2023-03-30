package survival2d.network;

import com.google.flatbuffers.FlatBufferBuilder;
import com.google.gson.Gson;
import com.miti99.netty.fbs.LoginRequest;
import com.miti99.netty.fbs.LoginResponse;
import com.miti99.netty.fbs.Packet;
import com.miti99.netty.fbs.PacketData;
import com.miti99.netty.fbs.Player;
import com.miti99.netty.fbs.Vec2;
import com.miti99.netty.json.request.BaseJsonRequest;
import com.miti99.netty.json.request.LoginJsonRequest;
import com.miti99.netty.json.response.LoginJsonResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

@ChannelHandler.Sharable
@Slf4j
public class WebsocketHandler extends ChannelInboundHandlerAdapter {

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    if (msg instanceof Packet packet) {
      if (packet.dataType() == PacketData.LoginRequest) {
        var request = new LoginRequest();
        packet.data(request);
        var builder = new FlatBufferBuilder(0);

        var usernameOffset = builder.createString("user_" + request.userId());
        Player.startPlayer(builder);
        Player.addUserId(builder, request.userId());
        Player.addUserName(builder, usernameOffset);
        Player.addPosition(builder, Vec2.createVec2(builder, 12, 2));
        Player.addRotation(builder, 12.02);
        Player.addTeam(builder, 1999);
        var playerOffset = Player.endPlayer(builder);

        var responseOffset = LoginResponse.createLoginResponse(builder, playerOffset);

        Packet.startPacket(builder);
        Packet.addDataType(builder, PacketData.LoginResponse);
        Packet.addData(builder, responseOffset);
        var packetOffset = Packet.endPacket(builder);
        builder.finish(packetOffset);

        var bytes = builder.dataBuffer();
        NetworkUtil.sendBinaryResponse(ctx.channel(), bytes);
      } else {
        log.warn("unknown packet data type: {}", packet.dataType());
      }
    } else if (msg instanceof TextWebSocketFrame textWebSocketFrame) {
      try {
        BaseJsonRequest request = BaseJsonRequest.fromJson(textWebSocketFrame.text());
        if (request instanceof LoginJsonRequest loginRequest) {
          var response =
              new LoginJsonResponse(loginRequest.getUserId(), "user_" + loginRequest.getUserId());
          NetworkUtil.sendJsonResponse(ctx.channel(), response);
        } else {
          NetworkUtil.sendTextResponse(ctx.channel(), new Gson().toJson(request));
        }
      } catch (Exception e) {
        log.error("can not parse text message: {}", textWebSocketFrame.text(), e);
      }
    } else {
      ctx.fireChannelRead(msg);
    }
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    super.channelReadComplete(ctx);
    ctx.flush();
  }
}
