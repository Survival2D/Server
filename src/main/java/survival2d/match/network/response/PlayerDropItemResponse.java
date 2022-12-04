package survival2d.match.network.response;

import survival2d.match.entity.base.Item;
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
public class PlayerDropItemResponse {

  private String username;

  private Item item;

  @EzyWriterImpl
  public static class ResponseWriter implements EzyWriter<PlayerDropItemResponse, EzyHashMap> {

    @Override
    public EzyHashMap write(EzyMarshaller ezyMarshaller, PlayerDropItemResponse response) {
      val data = "{map: " + GsonHolder.getNormalGson().toJson(response) + "}";
      val map = GsonHolder.getNormalGson().fromJson(data, EzyHashMap.class);
      return map;
    }
  }
}
