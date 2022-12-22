package survival2d.misc;

import com.google.flatbuffers.FlatBufferBuilder;
import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyfoxserver.context.EzyZoneContext;
import com.tvd12.ezyfoxserver.controller.EzyAbstractZoneEventController;
import com.tvd12.ezyfoxserver.event.EzyStreamingEvent;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import survival2d.flatbuffers.Packet;
import survival2d.flatbuffers.PacketData;
import survival2d.flatbuffers.PlayerAttackRequest;
import survival2d.flatbuffers.PlayerChangeWeaponRequest;
import survival2d.flatbuffers.PlayerMoveRequest;
import survival2d.flatbuffers.SetAutoPlayRequest;
import survival2d.flatbuffers.UseHealItemRequest;
import survival2d.match.action.PlayerAttack;
import survival2d.match.action.PlayerChangeWeapon;
import survival2d.match.action.PlayerMove;
import survival2d.match.action.PlayerReloadWeapon;
import survival2d.match.action.PlayerTakeItem;
import survival2d.match.action.PlayerUseHealItem;
import survival2d.misc.util.SamplePingData;
import survival2d.util.ezyfox.EzyFoxUtil;
import survival2d.util.stream.ByteBufferUtil;

@EzySingleton
@Slf4j
public class StreamingController extends EzyAbstractZoneEventController<EzyStreamingEvent> {

  @Override
  public void handle(EzyZoneContext ezyZoneContext, EzyStreamingEvent ezyStreamingEvent) {
    val username = ezyStreamingEvent.getSession().getOwnerName();
    val buf = ByteBufferUtil.ezyFoxBytesToByteBuffer(ezyStreamingEvent.getBytes());
    val packet = Packet.getRootAsPacket(buf);
    switch (packet.dataType()) {
      case PacketData.MatchInfoRequest:
        {
          val optMatch = EzyFoxUtil.getMatchingService().getMatchOfPlayer(username);
          if (!optMatch.isPresent()) {
            log.warn("match is not present");
            return;
          }
          val match = optMatch.get();
          match.responseMatchInfoOnStart(username);
          break;
        }
      case PacketData.PlayerMoveRequest:
        {
          val optMatch = EzyFoxUtil.getMatchingService().getMatchOfPlayer(username);
          if (!optMatch.isPresent()) {
            log.warn("match is not present");
            return;
          }
          val request = new PlayerMoveRequest();
          packet.data(request);

          val match = optMatch.get();
          match.onReceivePlayerAction(
              username,
              new PlayerMove(
                  new Vector2D(request.direction().x(), request.direction().y()),
                  request.rotation()));
          break;
        }
      case PacketData.PlayerChangeWeaponRequest:
        {
          val optMatch = EzyFoxUtil.getMatchingService().getMatchOfPlayer(username);
          if (!optMatch.isPresent()) {
            log.warn("match is not present");
            return;
          }
          val request = new PlayerChangeWeaponRequest();
          packet.data(request);

          val match = optMatch.get();
          match.onReceivePlayerAction(username, new PlayerChangeWeapon(request.slot()));
          break;
        }
      case PacketData.PlayerAttackRequest:
        {
          val optMatch = EzyFoxUtil.getMatchingService().getMatchOfPlayer(username);
          if (!optMatch.isPresent()) {
            log.warn("match is not present");
            return;
          }
          val request = new PlayerAttackRequest();
          packet.data(request);
          val match = optMatch.get();
          match.onReceivePlayerAction(username, new PlayerAttack());
          break;
        }
      case PacketData.PlayerReloadWeaponRequest:
        {
          val optMatch = EzyFoxUtil.getMatchingService().getMatchOfPlayer(username);
          if (!optMatch.isPresent()) {
            log.warn("match is not present");
            return;
          }
          val match = optMatch.get();
          match.onReceivePlayerAction(username, new PlayerReloadWeapon());
          break;
        }
      case PacketData.PlayerTakeItemRequest:
        {
          val optMatch = EzyFoxUtil.getMatchingService().getMatchOfPlayer(username);
          if (!optMatch.isPresent()) {
            log.warn("match is not present");
            return;
          }
          val match = optMatch.get();
          match.onReceivePlayerAction(username, new PlayerTakeItem());
          break;
        }
      case PacketData.UseHealItemRequest:{
        val optMatch = EzyFoxUtil.getMatchingService().getMatchOfPlayer(username);
        if (!optMatch.isPresent()) {
          log.warn("match is not present");
          return;
        }
        val request = new UseHealItemRequest();
        packet.data(request);

        val match = optMatch.get();
        match.onReceivePlayerAction(username, new PlayerUseHealItem(request.type()));
        break;
      }
      case PacketData.SetAutoPlayRequest:
        {
          val optMatch = EzyFoxUtil.getMatchingService().getMatchOfPlayer(username);
          if (!optMatch.isPresent()) {
            log.warn("match is not present");
            return;
          }
          val request = new SetAutoPlayRequest();
          packet.data(request);

          val match = optMatch.get();
          match.setPlayerAutoPlay(username, request.enable());
          break;
        }
      case PacketData.PingRequest:
        {
          val builder = new FlatBufferBuilder(0);
          survival2d.flatbuffers.PingResponse.startPingResponse(builder);
          val responseOffset = survival2d.flatbuffers.PingResponse.endPingResponse(builder);

          Packet.startPacket(builder);
          Packet.addDataType(builder, PacketData.PingResponse);
          Packet.addData(builder, responseOffset);
          val packetOffset = Packet.endPacket(builder);
          builder.finish(packetOffset);

          val bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
          ezyZoneContext.stream(bytes, ezyStreamingEvent.getSession());
          log.info("pingByte's size {}", bytes.length);
          break;
        }
      case PacketData.PingByPlayerMoveRequest:
        {
          val builder = new FlatBufferBuilder(0);
          val usernameOffset = builder.createString(SamplePingData.username);
          survival2d.flatbuffers.PingByPlayerMoveResponse.startPingByPlayerMoveResponse(builder);
          survival2d.flatbuffers.PingByPlayerMoveResponse.addUsername(builder, usernameOffset);
          survival2d.flatbuffers.Vec2.createVec2(
              builder, SamplePingData.position.getX(), SamplePingData.position.getY());
          survival2d.flatbuffers.PingByPlayerMoveResponse.addRotation(
              builder, SamplePingData.rotation);
          val responseOffset = survival2d.flatbuffers.PingResponse.endPingResponse(builder);

          Packet.startPacket(builder);
          Packet.addDataType(builder, PacketData.PingByPlayerMoveResponse);
          Packet.addData(builder, responseOffset);
          val packetOffset = Packet.endPacket(builder);
          builder.finish(packetOffset);

          val bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
          ezyZoneContext.stream(bytes, ezyStreamingEvent.getSession());
          log.info("pingByPlayerMoveByte's size {}", bytes.length);
          break;
        }
      case PacketData.PingByMatchInfoRequest:
        {
          val builder = new FlatBufferBuilder(0);

          final int responseOffset = SamplePingData.match.putMatchInfoData(builder);

          Packet.startPacket(builder);
          Packet.addDataType(builder, PacketData.PingByMatchInfoResponse);
          Packet.addData(builder, responseOffset);
          val packetOffset = Packet.endPacket(builder);
          builder.finish(packetOffset);

          val bytes = ByteBufferUtil.byteBufferToEzyFoxBytes(builder.dataBuffer());
          ezyZoneContext.stream(bytes, ezyStreamingEvent.getSession());
          log.info("pingByMatchInfoByte's size {}", bytes.length);
          break;
        }
      default:
        {
          log.warn("not handle packet data type {} from user {}", packet.dataType(), username);
          break;
        }
    }
  }
}
