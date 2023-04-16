package survival2d.misc;

import com.google.flatbuffers.FlatBufferBuilder;
import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyfoxserver.context.EzyZoneContext;
import com.tvd12.ezyfoxserver.controller.EzyAbstractZoneEventController;
import com.tvd12.ezyfoxserver.event.EzyStreamingEvent;
import lombok.extern.slf4j.Slf4j;
import lombok.var;

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
import survival2d.misc.util.SamplePingData;
import survival2d.util.stream.ByteBufferUtil;

@EzySingleton
@Slf4j
public class StreamingController extends EzyAbstractZoneEventController<EzyStreamingEvent> {

  @Override
  public void handle(EzyZoneContext ezyZoneContext, EzyStreamingEvent ezyStreamingEvent) {
    var username = ezyStreamingEvent.getSession().getOwnerName();
    var buf = ByteBufferUtil.ezyFoxBytesToByteBuffer(ezyStreamingEvent.getBytes());
    var packet = Packet.getRootAsPacket(buf);

  }
}
