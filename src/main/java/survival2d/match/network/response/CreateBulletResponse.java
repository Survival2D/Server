package survival2d.match.network.response;

import com.tvd12.ezyfox.binding.EzyMarshaller;
import com.tvd12.ezyfox.binding.EzyWriter;
import com.tvd12.ezyfox.binding.annotation.EzyWriterImpl;
import com.tvd12.ezyfox.entity.EzyHashMap;
import lombok.Builder;
import lombok.Data;
import lombok.var;
import survival2d.match.entity.Bullet;
import survival2d.util.serialize.GsonHolder;

@Data
@Builder
public class CreateBulletResponse {

  private Bullet bullet;

  @EzyWriterImpl
  public static class CreateBulletResponseResponseWriter implements
      EzyWriter<CreateBulletResponse, EzyHashMap> {

    @Override
    public EzyHashMap write(EzyMarshaller ezyMarshaller, CreateBulletResponse response) {
      var data = "{map: " + GsonHolder.getNormalGson().toJson(response) + "}";
      var map = GsonHolder.getNormalGson().fromJson(data, EzyHashMap.class);
      return map;
    }
  }
}
