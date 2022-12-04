package survival2d.controller.server;

import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyfoxserver.context.EzyZoneContext;
import com.tvd12.ezyfoxserver.controller.EzyAbstractZoneEventController;
import com.tvd12.ezyfoxserver.event.EzyStreamingEvent;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.locationtech.jts.math.Vector2D;
import survival2d.flatbuffers.Packet;
import survival2d.flatbuffers.PacketData;
import survival2d.flatbuffers.PlayerAttackRequest;
import survival2d.flatbuffers.PlayerChangeWeaponRequest;
import survival2d.flatbuffers.PlayerMoveRequest;
import survival2d.match.action.PlayerAttack;
import survival2d.match.action.PlayerChangeWeapon;
import survival2d.match.action.PlayerMove;
import survival2d.match.action.PlayerReloadWeapon;
import survival2d.match.action.PlayerTakeItem;
import survival2d.service.MatchingService;
import survival2d.util.BeanUtil;
import survival2d.util.stream.ByteBufferUtil;

@EzySingleton
// @EzyEventHandler(STREAMING)
//@EzyBeanPackagesToScan(value = "survival2d")
@Slf4j
public class StreamingController extends EzyAbstractZoneEventController<EzyStreamingEvent> {

  @EzyAutoBind("matchingService")
  MatchingService matchingService;

  @Override
  public void handle(EzyZoneContext ezyZoneContext, EzyStreamingEvent ezyStreamingEvent) {
    val username = ezyStreamingEvent.getSession().getOwnerName();
    val buf = ByteBufferUtil.ezyFoxBytesToByteBuffer(ezyStreamingEvent.getBytes());
    val packet = Packet.getRootAsPacket(buf);
    switch (packet.dataType()) {
      case PacketData.MatchInfoRequest: {
        val optMatch = BeanUtil.getMatchingService().getMatchOfPlayer(username);
        if (!optMatch.isPresent()) {
          log.warn("match is not present");
          return;
        }
        val match = optMatch.get();
        match.responseMatchInfo(username);
        break;
      }
      case PacketData.PlayerMoveRequest: {
        val optMatch = BeanUtil.getMatchingService().getMatchOfPlayer(username);
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
      case PacketData.PlayerChangeWeaponRequest: {
        val optMatch = BeanUtil.getMatchingService().getMatchOfPlayer(username);
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
      case PacketData.PlayerAttackRequest: {
        val optMatch = BeanUtil.getMatchingService().getMatchOfPlayer(username);
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
      case PacketData.PlayerReloadWeaponRequest: {
        val optMatch = BeanUtil.getMatchingService().getMatchOfPlayer(username);
        if (!optMatch.isPresent()) {
          log.warn("match is not present");
          return;
        }
        val match = optMatch.get();
        match.onReceivePlayerAction(username, new PlayerReloadWeapon());
        break;
      }
      case PacketData.PlayerTakeItemRequest: {
        val optMatch = BeanUtil.getMatchingService().getMatchOfPlayer(username);
        if (!optMatch.isPresent()) {
          log.warn("match is not present");
          return;
        }
        val match = optMatch.get();
        match.onReceivePlayerAction(username, new PlayerTakeItem());
        break;
      }
      default: {
        log.warn("not handle packet data type {} from user {}", packet.dataType(), username);
        break;
      }
    }
  }
}
