package survival2d.network;

import com.badlogic.gdx.math.Vector2;
import com.google.flatbuffers.FlatBufferBuilder;
import com.google.gson.Gson;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.internal.ChannelUtils;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler.HandshakeComplete;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import survival2d.data.ServerData;
import survival2d.flatbuffers.PingByPlayerMoveResponse;
import survival2d.flatbuffers.PingResponse;
import survival2d.flatbuffers.PlayerAttackRequest;
import survival2d.flatbuffers.PlayerChangeWeaponRequest;
import survival2d.flatbuffers.PlayerMoveRequest;
import survival2d.flatbuffers.Request;
import survival2d.flatbuffers.RequestUnion;
import survival2d.flatbuffers.Response;
import survival2d.flatbuffers.ResponseUnion;
import survival2d.flatbuffers.Vector2Struct;
import survival2d.match.action.PlayerAttack;
import survival2d.match.action.PlayerChangeWeapon;
import survival2d.match.action.PlayerMove;
import survival2d.match.action.PlayerReloadWeapon;
import survival2d.match.action.PlayerTakeItem;
import survival2d.network.client.User;
import survival2d.network.json.request.BaseJsonRequest;
import survival2d.network.json.request.LoginJsonRequest;
import survival2d.network.json.response.LoginJsonResponse;
import survival2d.ping.data.SamplePingData;
import survival2d.service.MatchingService;

@Sharable
@Slf4j
public class WebsocketHandler extends ChannelInboundHandlerAdapter {
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    if (msg instanceof Request request) {
      var userId = getUserId(ctx.channel());
      switch (request.requestType()) {
        case RequestUnion.MatchInfoRequest -> {
          var optMatch = MatchingService.getInstance().getMatchOfUser(userId);
          if (optMatch.isEmpty()) {
            log.warn("match is not present");
            break;
          }
          var match = optMatch.get();
          match.responseMatchInfoOnStart(userId);
        }
        case RequestUnion.PlayerMoveRequest -> {
          var optMatch = MatchingService.getInstance().getMatchOfUser(userId);
          if (optMatch.isEmpty()) {
            log.warn("match is not present");
            break;
          }
          var playerMoveRequest = new PlayerMoveRequest();
          request.request(request);

          var match = optMatch.get();
          match.onReceivePlayerAction(
              userId,
              new PlayerMove(
                  new Vector2(playerMoveRequest.direction().x(), playerMoveRequest.direction().y()),
                  playerMoveRequest.rotation()));
        }
        case RequestUnion.PlayerChangeWeaponRequest -> {
          var optMatch = MatchingService.getInstance().getMatchOfUser(userId);
          if (optMatch.isEmpty()) {
            log.warn("match is not present");
            break;
          }
          var playerChangeWeaponRequest = new PlayerChangeWeaponRequest();
          request.request(request);

          var match = optMatch.get();
          match.onReceivePlayerAction(
              userId, new PlayerChangeWeapon(playerChangeWeaponRequest.slot()));
        }
        case RequestUnion.PlayerAttackRequest -> {
          var optMatch = MatchingService.getInstance().getMatchOfUser(userId);
          if (optMatch.isEmpty()) {
            log.warn("match is not present");
            break;
          }
          var playerAttackRequest = new PlayerAttackRequest();
          request.request(playerAttackRequest);
          var match = optMatch.get();
          match.onReceivePlayerAction(userId, new PlayerAttack());
        }
        case RequestUnion.PlayerReloadWeaponRequest -> {
          var optMatch = MatchingService.getInstance().getMatchOfUser(userId);
          if (optMatch.isEmpty()) {
            log.warn("match is not present");
            return;
          }
          var match = optMatch.get();
          match.onReceivePlayerAction(userId, new PlayerReloadWeapon());
        }
        case RequestUnion.PlayerTakeItemRequest -> {
          var optMatch = MatchingService.getInstance().getMatchOfUser(userId);
          if (optMatch.isEmpty()) {
            log.warn("match is not present");
            return;
          }
          var match = optMatch.get();
          match.onReceivePlayerAction(userId, new PlayerTakeItem());
        }
        case RequestUnion.PingRequest -> {
          var builder = new FlatBufferBuilder(0);
          PingResponse.startPingResponse(builder);
          var responseOffset = PingResponse.endPingResponse(builder);

          Response.startResponse(builder);
          Response.addResponseType(builder, ResponseUnion.PingResponse);
          Response.addResponse(builder, responseOffset);
          var packetOffset = Response.endResponse(builder);
          builder.finish(packetOffset);

          NetworkUtil.sendResponse(userId, builder.dataBuffer());
        }
        case RequestUnion.PingByPlayerMoveRequest -> {
          var builder = new FlatBufferBuilder(0);
          PingByPlayerMoveResponse.startPingByPlayerMoveResponse(builder);
          PingByPlayerMoveResponse.addPlayerId(builder, SamplePingData.userId);
          Vector2Struct.createVector2Struct(
              builder, SamplePingData.position.x, SamplePingData.position.y);
          PingByPlayerMoveResponse.addRotation(
              builder, SamplePingData.rotation);
          var responseOffset = PingResponse.endPingResponse(builder);

          Response.startResponse(builder);
          Response.addResponseType(builder, ResponseUnion.PingByPlayerMoveResponse);
          Response.addResponse(builder, responseOffset);
          var packetOffset = Response.endResponse(builder);
          builder.finish(packetOffset);

          var dataBuffer = builder.dataBuffer();
          NetworkUtil.sendResponse(userId, dataBuffer);
          var data = dataBuffer.array();
          log.info("pingByPlayerMoveByte's size {}", data.length);
        }
        case RequestUnion.PingByMatchInfoRequest -> {
          var builder = new FlatBufferBuilder(0);

          final int responseOffset = SamplePingData.match.putMatchInfoData(builder);

          Response.startResponse(builder);
          Response.addResponseType(builder, ResponseUnion.PingByMatchInfoResponse);
          Response.addResponse(builder, responseOffset);
          var packetOffset = Response.endResponse(builder);
          builder.finish(packetOffset);

          var dataBuffer = builder.dataBuffer();
          NetworkUtil.sendResponse(userId, dataBuffer);
          var data = dataBuffer.array();
          log.info("pingByMatchInfoByte's size {}", data.length);
        }
        default ->
            log.warn("not handle requestType {} from user {}", request.requestType(), userId);
      }
    } else if (msg instanceof TextWebSocketFrame textWebSocketFrame) {
      try {
        BaseJsonRequest request = BaseJsonRequest.fromJson(textWebSocketFrame.text());
        if (request instanceof LoginJsonRequest loginRequest) {
          var response =
              new LoginJsonResponse(loginRequest.getUserId(), "user_" + loginRequest.getUserId());
          NetworkUtil.sendResponse(ctx.channel(), response);
        } else {
          NetworkUtil.sendResponse(ctx.channel(), new Gson().toJson(request));
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
      var user = new User(getUserId(ctx.channel()), channel);
      user.setName(String.valueOf(user.getId()));

      ServerData.getInstance().getUserMap().put(user.getId(), user);
      log.info("User {} connected", user.getId());
      new Thread(
              () -> {
                //TODO
//                try {
//                  Thread.sleep(2000L);
//                  NetworkUtil.sendBinaryResponse(
//                      channel,
//                      ClientEventCode.CODE_CLIENT_CONNECT,
//                      String.valueOf(user.getId()));
//                  ChannelUtils.pushToClient(
//                      channel, ClientEventCode.CODE_CLIENT_NICKNAME_SET, null);
//                } catch (InterruptedException ignored) {
//                }
              })
          .start();
    } else {
      super.userEventTriggered(ctx, evt);
    }
  }

  private int getUserId(Channel channel) {
    var channelId = channel.id().asLongText();
    var userId = ServerData.getInstance().getChannelMap().get(channelId);
    if (userId == null) {
      userId = ServerData.getInstance().newUserId();
      ServerData.getInstance().getChannelMap().put(channelId, userId);
    }
    return userId;
  }

  private void onReaderIdle(Channel channel) {
    var userId = getUserId(channel);
    var user = ServerData.getInstance().getUserMap().get(userId);
    if (user != null) {
      log.info("onReaderIdle: userId {}, userName {}", userId, user.getName());
      //TODO
//      ServerEventListener.get(ServerEventCode.CODE_CLIENT_OFFLINE).call(user, null);
    }
  }
}
