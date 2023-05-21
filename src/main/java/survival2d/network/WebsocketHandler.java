package survival2d.network;

import com.badlogic.gdx.math.Vector2;
import com.google.flatbuffers.FlatBufferBuilder;
import com.google.gson.Gson;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler.HandshakeComplete;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import survival2d.data.ServerData;
import survival2d.flatbuffers.*;
import survival2d.match.action.PlayerAttack;
import survival2d.match.action.PlayerChangeWeapon;
import survival2d.match.action.PlayerMove;
import survival2d.match.action.PlayerReloadWeapon;
import survival2d.match.action.PlayerTakeItem;
import survival2d.network.json.request.BaseJsonRequest;
import survival2d.network.json.request.LoginJsonRequest;
import survival2d.network.json.response.LoginJsonResponse;
import survival2d.ping.data.SamplePingData;
import survival2d.service.FindMatchService;
import survival2d.service.LobbyTeamService;
import survival2d.service.MatchingService;

@Sharable
@Slf4j
public class WebsocketHandler extends ChannelInboundHandlerAdapter {
  private void createTeam(int userId) {
    var teamId = LobbyTeamService.getInstance().createTeam();
    LobbyTeamService.getInstance().joinTeam(userId, teamId);

    var builder = new FlatBufferBuilder(0);
    var createTeamResponse = CreateTeamResponse.createCreateTeamResponse(builder, teamId);

    Response.startResponse(builder);
    Response.addResponseType(builder, ResponseUnion.CreateTeamResponse);
    Response.addResponse(builder, createTeamResponse);
    var response = Response.endResponse(builder);
    builder.finish(response);

    var dataBuffer = builder.dataBuffer();
    NetworkUtil.sendResponse(userId, dataBuffer);
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    if (msg instanceof Request request) {
      handleBinaryData(ctx, request);
    } else if (msg instanceof TextWebSocketFrame textWebSocketFrame) {
      handleTextData(ctx, textWebSocketFrame);
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
    if (evt instanceof HandshakeComplete) {
      onHandshakeComplete(ctx.channel());
    } else if (evt instanceof IdleStateEvent event) {
      if (event.state() == IdleState.READER_IDLE) {
        try {
          onReaderIdle(ctx.channel());
          ctx.channel().close();
        } catch (Exception e) {
          log.error("onReaderIdle error", e);
        }
      }
    } else {
      super.userEventTriggered(ctx, evt);
    }
  }

  private void onHandshakeComplete(Channel channel) {
    var user = ServerData.getInstance().newUser(channel);
    log.info("User {} connected", user.getId());
    //    new Thread(
    //            () -> {
    //              try {
    //                Thread.sleep(1000);
    //              } catch (InterruptedException e) {
    //                log.error("onHandshakeComplete error", e);
    //              }
    //              var builder = new FlatBufferBuilder(0);
    //              var responseOffset = LoginResponse.createLoginResponse(builder, user.getId());
    //
    //              Response.startResponse(builder);
    //              Response.addResponseType(builder, ResponseUnion.LoginResponse);
    //              Response.addResponse(builder, responseOffset);
    //              var packetOffset = Response.endResponse(builder);
    //              builder.finish(packetOffset);
    //
    //              var dataBuffer = builder.dataBuffer();
    //              NetworkUtil.sendResponse(user.getId(), dataBuffer);
    //            })
    //        .start();
  }

  private void handleBinaryData(ChannelHandlerContext ctx, Request request) {
    var channel = ctx.channel();
    var user = ServerData.getInstance().getUser(channel);
    var userId = user.getId();
    switch (request.requestType()) {
      case RequestUnion.LoginRequest -> {
        var loginRequest = new LoginRequest();
        request.request(loginRequest);

        var userName = loginRequest.userName();
        user.setName(userName);
        log.info("User {} login with name {}", userId, userName);

        var builder = new FlatBufferBuilder(0);
        var nameOffset = builder.createString(userName);
        var loginResponse = LoginResponse.createLoginResponse(builder, userId, nameOffset);

        Response.startResponse(builder);
        Response.addResponseType(builder, ResponseUnion.LoginResponse);
        Response.addResponse(builder, loginResponse);
        var response = Response.endResponse(builder);
        builder.finish(response);

        var dataBuffer = builder.dataBuffer();
        NetworkUtil.sendResponse(userId, dataBuffer);
      }
      case RequestUnion.CreateTeamRequest -> {
        createTeam(userId);
      }
      case RequestUnion.JoinTeamRequest -> {
        var joinTeamRequest = new JoinTeamRequest();
        request.request(joinTeamRequest);
        var teamId = joinTeamRequest.teamId();
        var optTeam = LobbyTeamService.getInstance().getTeam(teamId);
        if (optTeam.isEmpty()) {
          var builder = new FlatBufferBuilder(0);
          JoinTeamResponse.startJoinTeamResponse(builder);
          var joinTeamResponse = JoinTeamResponse.endJoinTeamResponse(builder);

          var response =
              Response.createResponse(
                  builder,
                  ResponseErrorEnum.TEAM_NOT_FOUND,
                  ResponseUnion.JoinTeamResponse,
                  joinTeamResponse);
          builder.finish(response);

          var dataBuffer = builder.dataBuffer();
          NetworkUtil.sendResponse(userId, dataBuffer);
          return;
        }
        var team = optTeam.get();
        var teamMemberIds = team.getMemberIds();
        var result = LobbyTeamService.getInstance().joinTeam(userId, teamId);
        if (!result) {
          var builder = new FlatBufferBuilder(0);
          JoinTeamResponse.startJoinTeamResponse(builder);
          var joinTeamResponse = JoinTeamResponse.endJoinTeamResponse(builder);

          var response =
              Response.createResponse(
                  builder,
                  ResponseErrorEnum.JOIN_TEAM_ERROR,
                  ResponseUnion.JoinTeamResponse,
                  joinTeamResponse);
          builder.finish(response);

          var dataBuffer = builder.dataBuffer();
          NetworkUtil.sendResponse(userId, dataBuffer);
          return;
        }
        {
          // Response to player who join team
          var builder = new FlatBufferBuilder(0);
          var joinTeamResponse = JoinTeamResponse.createJoinTeamResponse(builder, teamId);

          var response =
              Response.createResponse(
                  builder,
                  ResponseErrorEnum.SUCCESS,
                  ResponseUnion.JoinTeamResponse,
                  joinTeamResponse);
          builder.finish(response);

          var dataBuffer = builder.dataBuffer();
          NetworkUtil.sendResponse(userId, dataBuffer);
        }
        {
          // Response to other player in team
          var builder = new FlatBufferBuilder(0);
          var userName = builder.createString(user.getName());
          var newUserJoinTeamResponse =
              NewUserJoinTeamResponse.createNewUserJoinTeamResponse(builder, userId, userName);

          var response =
              Response.createResponse(
                  builder,
                  ResponseErrorEnum.SUCCESS,
                  ResponseUnion.NewUserJoinTeamResponse,
                  newUserJoinTeamResponse);
          builder.finish(response);

          var dataBuffer = builder.dataBuffer();
          NetworkUtil.sendResponse(teamMemberIds, dataBuffer);
        }
      }
      case RequestUnion.FindMatchRequest -> {
        var optTeam = LobbyTeamService.getInstance().getTeamOfPlayer(userId);
        if (optTeam.isEmpty()) {
          createTeam(userId);
          optTeam = LobbyTeamService.getInstance().getTeamOfPlayer(userId);
        }
        var optMatchId = FindMatchService.getInstance().findMatch(optTeam.get().getId());
        if (optMatchId.isEmpty()) {
          log.warn("matchId is not present");
          break;
        }
        var matchId = optMatchId.get();
        var builder = new FlatBufferBuilder(0);
        var findMatchResponse = FindMatchResponse.createFindMatchResponse(builder, matchId);

        Response.startResponse(builder);
        Response.addResponseType(builder, ResponseUnion.FindMatchResponse);
        Response.addResponse(builder, findMatchResponse);
        var response = Response.endResponse(builder);
        builder.finish(response);

        var match = MatchingService.getInstance().getMatchById(matchId).get();

        NetworkUtil.sendResponse(match.getAllPlayerIds(), builder.dataBuffer());

        match.responseMatchInfoOnStart();
      }
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
        request.request(playerMoveRequest);

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
        request.request(playerChangeWeaponRequest);

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
        PingByPlayerMoveResponse.addPlayerId(builder, SamplePingData.playerId);
        Vector2Struct.createVector2Struct(
            builder, SamplePingData.position.x, SamplePingData.position.y);
        PingByPlayerMoveResponse.addRotation(builder, SamplePingData.rotation);
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
      default -> log.warn("not handle requestType {} from user {}", request.requestType(), userId);
    }
  }

  private void handleTextData(ChannelHandlerContext ctx, TextWebSocketFrame textWebSocketFrame) {
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
  }

  private void onReaderIdle(Channel channel) {
    var user = ServerData.getInstance().getUser(channel);
    if (user != null) {
      log.info("onReaderIdle: userId {}, userName {}", user.getId(), user.getName());
      // TODO
      //      ServerEventListener.get(ServerEventCode.CODE_CLIENT_OFFLINE).call(user, null);
    }
  }
}
