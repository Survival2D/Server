package survival2d.lobby.network.response;

import com.tvd12.ezyfox.binding.EzyMarshaller;
import com.tvd12.ezyfox.binding.EzyWriter;
import com.tvd12.ezyfox.binding.annotation.EzyWriterImpl;
import com.tvd12.ezyfox.entity.EzyHashMap;
import lombok.Builder;
import lombok.Data;
import lombok.val;
import survival2d.common.ResponseError;
import survival2d.util.serialize.GsonHolder;

@Data
@Builder
public class FindMatchResponse {

  ResponseError result;
  long matchId;

  @EzyWriterImpl
  public static class FindMatchResponseWriter implements EzyWriter<FindMatchResponse, EzyHashMap> {

    @Override
    public EzyHashMap write(EzyMarshaller ezyMarshaller, FindMatchResponse findMatchResponse) {
      val data = "{map: " + GsonHolder.getResponseGson().toJson(findMatchResponse) + "}";
      val map = GsonHolder.getResponseGson().fromJson(data, EzyHashMap.class);
      return map;
    }
  }
}
