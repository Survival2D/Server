package survival2d.match.network.response;

import com.tvd12.ezyfox.binding.EzyMarshaller;
import com.tvd12.ezyfox.binding.EzyWriter;
import com.tvd12.ezyfox.binding.annotation.EzyWriterImpl;
import com.tvd12.ezyfox.entity.EzyHashMap;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import survival2d.util.packet.PacketUtil;

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
