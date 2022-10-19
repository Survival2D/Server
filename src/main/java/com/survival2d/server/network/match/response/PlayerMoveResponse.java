package com.survival2d.server.network.match.response;

import com.survival2d.server.util.packet.PacketUtil;
import com.tvd12.ezyfox.binding.EzyMarshaller;
import com.tvd12.ezyfox.binding.EzyWriter;
import com.tvd12.ezyfox.binding.annotation.EzyWriterImpl;
import com.tvd12.ezyfox.entity.EzyHashMap;
import lombok.Builder;
import lombok.Data;
import org.locationtech.jts.math.Vector2D;

@Data
@Builder
public class PlayerMoveResponse {

  private String username;
  private Vector2D position;
  private double rotation;

  @EzyWriterImpl
  public static class Writer implements EzyWriter<PlayerMoveResponse, EzyHashMap> {
    @Override
    public EzyHashMap write(EzyMarshaller ezyMarshaller, PlayerMoveResponse playerMoveResponse) {
      return PacketUtil.toEzyHashMap(playerMoveResponse);
    }
  }
}
