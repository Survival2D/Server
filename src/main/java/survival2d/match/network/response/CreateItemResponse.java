package survival2d.match.network.response;

import com.tvd12.ezyfox.binding.EzyMarshaller;
import com.tvd12.ezyfox.binding.EzyWriter;
import com.tvd12.ezyfox.binding.annotation.EzyWriterImpl;
import com.tvd12.ezyfox.entity.EzyHashMap;
import lombok.Builder;
import lombok.Data;
import lombok.var;
import survival2d.match.entity.ItemOnMap;
import survival2d.util.serialize.GsonHolder;

@Data
@Builder
public class CreateItemResponse {

  private ItemOnMap item;

  @EzyWriterImpl
  public static class CreateItemResponseResponseWriter implements
      EzyWriter<CreateItemResponse, EzyHashMap> {

    @Override
    public EzyHashMap write(EzyMarshaller ezyMarshaller, CreateItemResponse response) {
      var data = "{map: " + GsonHolder.getNormalGson().toJson(response) + "}";
      var map = GsonHolder.getNormalGson().fromJson(data, EzyHashMap.class);
      return map;
    }
  }
}
