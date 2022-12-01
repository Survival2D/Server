package survival2d.network;

import com.google.flatbuffers.FlatBufferBuilder;
import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyfoxserver.context.EzyZoneContext;
import com.tvd12.ezyfoxserver.controller.EzyAbstractZoneEventController;
import com.tvd12.ezyfoxserver.event.EzyStreamingEvent;
import java.nio.ByteBuffer;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.locationtech.jts.math.Vector2D;
import survival2d.flatbuffers.Packet;
import survival2d.flatbuffers.PacketData;
import survival2d.flatbuffers.PlayerMoveRequest;
import survival2d.flatbuffers.PlayerMoveResponse;
import survival2d.flatbuffers.Vec2;
import survival2d.game.action.PlayerMove;
import survival2d.service.MatchingService;

@EzySingleton
// @EzyEventHandler(STREAMING)
@Slf4j
public class StreamingController extends EzyAbstractZoneEventController<EzyStreamingEvent> {
  @EzyAutoBind MatchingService matchingService;

  @EzyAutoBind
  @Override
  public void handle(EzyZoneContext ezyZoneContext, EzyStreamingEvent ezyStreamingEvent) {
    val username = ezyStreamingEvent.getSession().getOwnerName();
    val buf = ByteBufferUtil.ezyFoxBytesToByteBuffer(ezyStreamingEvent.getBytes());
    val packet = Packet.getRootAsPacket(buf);
    switch (packet.dataType()) {
      case PacketData.MatchInfoRequest:
        {
          val optMatch = matchingService.getMatchOfPlayer(username);
          if (!optMatch.isPresent()) {
            log.warn("match is not present");
            return;
          }
          val match = optMatch.get();
          match.responseMatchInfo(username);
          break;
        }
      case PacketData.PlayerMoveRequest:
        {
          val optMatch = matchingService.getMatchOfPlayer(username);
          if (!optMatch.isPresent()) {
            log.warn("match is not present");
            return;
          }
          val request = new PlayerMoveRequest();
          packet.data(request);


          val match = optMatch.get();
          match.onReceivePlayerAction(
              username, new PlayerMove(new Vector2D(request.direction().x(), request.direction().y()), request.rotation()));
          break;
        }

      case PacketData.PlayerAttackRequest:
        {
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
