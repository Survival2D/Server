package survival2d.match.network.response;

import com.tvd12.ezyfox.binding.EzyMarshaller;
import com.tvd12.ezyfox.binding.EzyWriter;
import com.tvd12.ezyfox.binding.annotation.EzyWriterImpl;
import com.tvd12.ezyfox.entity.EzyHashMap;
import lombok.Builder;
import lombok.Data;
import lombok.val;
import survival2d.match.entity.Weapon;
import survival2d.util.serialize.GsonHolder;

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
