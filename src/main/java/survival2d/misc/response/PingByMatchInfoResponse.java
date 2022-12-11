package survival2d.misc.response;

import com.tvd12.ezyfox.binding.EzyMarshaller;
import com.tvd12.ezyfox.binding.EzyWriter;
import com.tvd12.ezyfox.binding.annotation.EzyWriterImpl;
import com.tvd12.ezyfox.entity.EzyHashMap;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import survival2d.match.entity.Match;
import survival2d.util.serialize.GsonHolder;

@Data
@Builder
@Slf4j
public class PingByMatchInfoResponse {
  Match match;

  @EzyWriterImpl
  public static class ResponseWriter implements EzyWriter<PingByMatchInfoResponse, EzyHashMap> {

    @Override
    public EzyHashMap write(EzyMarshaller ezyMarshaller, PingByMatchInfoResponse response) {
      val data = "{map: " + GsonHolder.getWithExcludeAnnotation().toJson(response) + "}";
      log.info("PingByMatchInfoResponse's length: {}", data.length());
      val map = GsonHolder.getNormalGson().fromJson(data, EzyHashMap.class);
      return map;
    }
  }
}
