package survival2d.network;

import com.google.flatbuffers.FlatBufferBuilder;
import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyfoxserver.context.EzyZoneContext;
import com.tvd12.ezyfoxserver.controller.EzyAbstractZoneEventController;
import com.tvd12.ezyfoxserver.event.EzyStreamingEvent;
import java.nio.ByteBuffer;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import survival2d.flatbuffers.Packet;
import survival2d.flatbuffers.PacketData;
import survival2d.flatbuffers.PlayerMoveRequest;
import survival2d.flatbuffers.PlayerMoveResponse;
import survival2d.flatbuffers.Vec2;

@EzySingleton
// @EzyEventHandler(STREAMING)
@Slf4j
public class StreamingController extends EzyAbstractZoneEventController<EzyStreamingEvent> {

  @Override
  public void handle(EzyZoneContext ezyZoneContext, EzyStreamingEvent ezyStreamingEvent) {
    val data =
        Arrays.copyOfRange(ezyStreamingEvent.getBytes(), 1, ezyStreamingEvent.getBytes().length);
    val buf = ByteBuffer.wrap(data);

    val packet = Packet.getRootAsPacket(buf);
    packet.dataType();
    switch (packet.dataType()) {
      case PacketData.PlayerMoveRequest:
        val username = ezyStreamingEvent.getSession().getOwnerName();
        PlayerMoveRequest request = new PlayerMoveRequest();
        packet.data(request);
        val direction = request.direction();
        val rotation = request.rotation();
//        log.info("direction: x {} y {}, rotation: {}", direction.x(), direction.y(), rotation);
        val builder = new FlatBufferBuilder(0);
        val idOffset = builder.createString(username);

        PlayerMoveResponse.startPlayerMoveResponse(builder);
        PlayerMoveResponse.addId(builder, idOffset);
        PlayerMoveResponse.addRotation(builder, rotation);
        val positionOffset = Vec2.createVec2(builder, direction.x(), direction.y());
        PlayerMoveResponse.addPosition(builder, positionOffset);
        val responseOffset = PlayerMoveResponse.endPlayerMoveResponse(builder);

        Packet.startPacket(builder);
        Packet.addDataType(builder, PacketData.PlayerMoveResponse);
        Packet.addData(builder, responseOffset);
        val packetOffset = Packet.endPacket(builder);
        builder.finish(packetOffset);

        val dataBuffer = builder.dataBuffer();
        val bytes = new byte[dataBuffer.remaining() + 1];
        bytes[0] = 0b00010000;
        dataBuffer.get(bytes, 1, dataBuffer.remaining());
//        log.info("data after add header: {}", bytes);
        ezyZoneContext.stream(bytes, ezyStreamingEvent.getSession());
        break;
    }
  }
}
