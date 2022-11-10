package com.survival2d.server.network.match.response;

import com.survival2d.server.game.entity.Weapon;
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
public class PlayerAttackResponse {

  private String username;
  private Weapon weapon;
  private Vector2D position;

  @EzyWriterImpl
  public static class Writer implements EzyWriter<PlayerAttackResponse, EzyHashMap> {
    @Override
    public EzyHashMap write(EzyMarshaller ezyMarshaller, PlayerAttackResponse response) {
      return PacketUtil.toEzyHashMap(response);
    }
  }
}
