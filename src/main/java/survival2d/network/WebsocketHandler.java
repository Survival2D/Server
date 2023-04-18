package survival2d.network;

import com.badlogic.gdx.math.Vector2;
import com.google.flatbuffers.FlatBufferBuilder;
import com.google.gson.Gson;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler.HandshakeComplete;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import survival2d.data.ServerData;
import survival2d.flatbuffers.PlayerAttackRequest;
import survival2d.flatbuffers.PlayerChangeWeaponRequest;
import survival2d.flatbuffers.PlayerMoveRequest;
import survival2d.flatbuffers.Request;
import survival2d.match.action.PlayerAttack;
import survival2d.match.action.PlayerChangeWeapon;
import survival2d.match.action.PlayerMove;
import survival2d.match.action.PlayerReloadWeapon;
import survival2d.match.action.PlayerTakeItem;
import survival2d.ping.data.SamplePingData;
import survival2d.network.client.User;
import survival2d.network.json.request.BaseJsonRequest;
import survival2d.network.json.request.LoginJsonRequest;
import survival2d.network.json.response.LoginJsonResponse;

@ChannelHandler.Sharable
@Slf4j
public class WebsocketHandler extends ChannelInboundHandlerAdapter {
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    if (msg instanceof Request request) {
      switch (request.requestType()) {
        case PacketData.MatchInfoRequest: {
          var optMatch = BeanUtil.getMatchingService().getMatchOfPlayer(username);
          if (!optMatch.isPresent()) {
            log.warn("match is not present");
            return;
          }
          var match = optMatch.get();
          match.responseMatchInfo(username);
          break;
        }
        case PacketData.PlayerMoveRequest: {
          var optMatch = BeanUtil.getMatchingService().getMatchOfPlayer(username);
          if (!optMatch.isPresent()) {
            log.warn("match is not present");
            return;
          }
          var request = new PlayerMoveRequest();
          packet.data(request);

          var match = optMatch.get();
          match.onReceivePlayerAction(
              username,
              new PlayerMove(
                  new Vector2(request.direction().x(), request.direction().y()),
                  request.rotation()));
          break;
        }
        case PacketData.PlayerChangeWeaponRequest: {
          var optMatch = BeanUtil.getMatchingService().getMatchOfPlayer(username);
          if (!optMatch.isPresent()) {
            log.warn("match is not present");
            return;
          }
          var request = new PlayerChangeWeaponRequest();
          packet.data(request);

          var match = optMatch.get();
          match.onReceivePlayerAction(username, new PlayerChangeWeapon(request.slot()));
          break;
        }
        case PacketData.PlayerAttackRequest: {
          var optMatch = BeanUtil.getMatchingService().getMatchOfPlayer(username);
          if (!optMatch.isPresent()) {
            log.warn("match is not present");
            return;
          }
          var request = new PlayerAttackRequest();
          packet.data(request);
          var match = optMatch.get();
          match.onReceivePlayerAction(username, new PlayerAttack());
          break;
        }
        case PacketData.PlayerReloadWeaponRequest: {
          var optMatch = BeanUtil.getMatchingService().getMatchOfPlayer(username);
          if (!optMatch.isPresent()) {
            log.warn("match is not present");
            return;
          }
          var match = optMatch.get();
          match.onReceivePlayerAction(username, new PlayerReloadWeapon());
          break;
        }
        case PacketData.PlayerTakeItemRequest: {
          var optMatch = BeanUtil.getMatchingService().getMatchOfPlayer(username);
          if (!optMatch.isPresent()) {
            log.warn("match is not present");
            return;
          }
          var match = optMatch.get();
          match.onReceivePlayerAction(username, new PlayerTakeItem());
          break;
        }
        case PacketData.PingRequest: {
          var builder = new FlatBufferBuilder(0);
          survival2d.flatbuffers.PingResponse.startPingResponse(builder);
          var responseOffset = survival2d.flatbuffers.PingResponse.endPingResponse(builder);

          Packet.startPacket(builder);
          Packet.addDataType(builder, PacketData.PingResponse);
          Packet.addData(builder, responseOffset);
          var packetOffset = Packet.endPacket(builder);
          builder.finish(packetOffset);

          var bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
          ezyZoneContext.stream(bytes, ezyStreamingEvent.getSession());
          log.info("pingByte's size {}", bytes.length);
          break;
        }
        case PacketData.PingByPlayerMoveRequest: {
          var builder = new FlatBufferBuilder(0);
          var usernameOffset = builder.createString(SamplePingData.username);
          survival2d.flatbuffers.PingByPlayerMoveResponse.startPingByPlayerMoveResponse(builder);
          survival2d.flatbuffers.PingByPlayerMoveResponse.addUsername(builder, usernameOffset);
          survival2d.flatbuffers.Vec2.createVec2(
              builder, SamplePingData.position.getX(), SamplePingData.position.getY());
          survival2d.flatbuffers.PingByPlayerMoveResponse.addRotation(
              builder, SamplePingData.rotation);
          var responseOffset = survival2d.flatbuffers.PingResponse.endPingResponse(builder);

          Packet.startPacket(builder);
          Packet.addDataType(builder, PacketData.PingByPlayerMoveResponse);
          Packet.addData(builder, responseOffset);
          var packetOffset = Packet.endPacket(builder);
          builder.finish(packetOffset);

          var bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
          ezyZoneContext.stream(bytes, ezyStreamingEvent.getSession());
          log.info("pingByPlayerMoveByte's size {}", bytes.length);
          break;
        }
        case PacketData.PingByMatchInfoRequest: {
          var builder = new FlatBufferBuilder(0);

          final int responseOffset = SamplePingData.match.putResponseData(builder);

          Packet.startPacket(builder);
          Packet.addDataType(builder, PacketData.PingByMatchInfoResponse);
          Packet.addData(builder, responseOffset);
          var packetOffset = Packet.endPacket(builder);
          builder.finish(packetOffset);

          var bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
          ezyZoneContext.stream(bytes, ezyStreamingEvent.getSession());
          log.info("pingByMatchInfoByte's size {}", bytes.length);
          break;
        }
        default: {
          log.warn("not handle packet data type {} from user {}", packet.dataType(), username);
          break;
        }
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

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof IdleStateEvent event) {
      if (event.state() == IdleState.READER_IDLE) {
        try {
          onReaderIdle(ctx.channel());
          ctx.channel().close();
        } catch (Exception e) {
          log.error("onReaderIdle error", e);
        }
      }
    } else if (evt instanceof HandshakeComplete) {
      var channel = ctx.channel();
      var user = new User(getId(ctx.channel()), channel);
      user.setName(String.valueOf(user.getId()));

      ServerData.getInstance().getUserMap().put(user.getId(), user);
      log.info("User {} connected", user.getId());
      new Thread(
              () -> {
                try {
                  Thread.sleep(2000L);
                  NetworkUtil.sendBinaryResponse(
                      channel,
                      ClientEventCode.CODE_CLIENT_CONNECT,
                      String.valueOf(user.getId()));
                  ChannelUtils.pushToClient(
                      channel, ClientEventCode.CODE_CLIENT_NICKNAME_SET, null);
                } catch (InterruptedException ignored) {
                }
              })
          .start();
    } else {
      super.userEventTriggered(ctx, evt);
    }
  }

  private int getId(Channel channel) {
    var channelId = channel.id().asLongText();
    var userId = ServerData.getInstance().getChannelMap().get(channelId);
    if (userId == null) {
      userId = ServerData.getInstance().newUserId();
      ServerData.getInstance().getChannelMap().put(channelId, userId);
    }
    return userId;
  }

  private void onReaderIdle(Channel channel) {
    var clientId = getId(channel);
    var user = ServerData.getInstance().getUserMap().get(clientId);
    if (user != null) {
      SimplePrinter.serverLog(
          "Has user exit to the server：" + clientId + " | " + user.getNickname());
      ServerEventListener.get(ServerEventCode.CODE_CLIENT_OFFLINE).call(user, null);
    }
  }
}
