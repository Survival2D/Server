package survival2d.network;

import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyfoxserver.context.EzyZoneContext;
import com.tvd12.ezyfoxserver.controller.EzyAbstractZoneEventController;
import com.tvd12.ezyfoxserver.event.EzyStreamingEvent;
import lombok.extern.slf4j.Slf4j;

import survival2d.flatbuffers.Packet;
import survival2d.flatbuffers.PacketData;
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
