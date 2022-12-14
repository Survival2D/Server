package survival2d.match.network.request;

import com.tvd12.ezyfox.binding.EzyReader;
import com.tvd12.ezyfox.binding.EzyUnmarshaller;
import com.tvd12.ezyfox.binding.annotation.EzyReaderImpl;
import lombok.Data;
import survival2d.util.serialize.GsonHolder;

@Data
public class PlayerDropItemRequest {

  String itemId;

  @EzyReaderImpl
  public static class RequestReader implements EzyReader<Object, PlayerDropItemRequest> {

    @Override
    public PlayerDropItemRequest read(EzyUnmarshaller ezyUnmarshaller, Object o) {
      return GsonHolder.getNormalGson().fromJson(o.toString(), PlayerDropItemRequest.class);
    }
  }
}
