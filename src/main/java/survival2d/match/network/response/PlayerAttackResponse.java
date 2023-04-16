package survival2d.match.network.response;

import com.tvd12.ezyfox.binding.EzyMarshaller;
import com.tvd12.ezyfox.binding.EzyWriter;
import com.tvd12.ezyfox.binding.annotation.EzyWriterImpl;
import com.tvd12.ezyfox.entity.EzyHashMap;
import lombok.Builder;
import lombok.Data;

import survival2d.match.entity.Weapon;


@Data
@Builder
public class PlayerAttackResponse {

  private String username;
  private Weapon weapon;
  private Vector2 position;

  @EzyWriterImpl
  public static class Writer implements EzyWriter<PlayerAttackResponse, EzyHashMap> {

    @Override
    public EzyHashMap write(EzyMarshaller ezyMarshaller, PlayerAttackResponse response) {
      return PacketUtil.toEzyHashMap(response);
    }
  }
}
