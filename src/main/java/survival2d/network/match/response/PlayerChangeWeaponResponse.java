package survival2d.network.match.response;

import survival2d.game.entity.Weapon;
import survival2d.util.serialize.GsonHolder;
import com.tvd12.ezyfox.binding.EzyMarshaller;
import com.tvd12.ezyfox.binding.EzyWriter;
import com.tvd12.ezyfox.binding.annotation.EzyWriterImpl;
import com.tvd12.ezyfox.entity.EzyHashMap;
import lombok.Builder;
import lombok.Data;
import lombok.val;

@Data
@Builder
public class PlayerChangeWeaponResponse {

  private String username;
  private int slot;
  private Weapon weapon; // TODO: implement chi tiết sau

  @EzyWriterImpl
  public static class PlayerChangeWeaponResponseWriter
      implements EzyWriter<PlayerChangeWeaponResponse, EzyHashMap> {

    @Override
    public EzyHashMap write(EzyMarshaller ezyMarshaller, PlayerChangeWeaponResponse weapon) {
      val data = "{map: " + GsonHolder.getNormalGson().toJson(weapon) + "}";
      val map = GsonHolder.getNormalGson().fromJson(data, EzyHashMap.class);
      return map;
    }
  }
}
