package survival2d.match.network.response;

import com.tvd12.ezyfox.binding.EzyMarshaller;
import com.tvd12.ezyfox.binding.EzyWriter;
import com.tvd12.ezyfox.binding.annotation.EzyWriterImpl;
import com.tvd12.ezyfox.entity.EzyHashMap;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import survival2d.match.entity.match.Match;
import survival2d.util.serialize.GsonHolder;

@Data
@Builder
@Slf4j
public class MatchInfoResponse {

  Match match;

  @EzyWriterImpl
  public static class MatchWriter implements EzyWriter<Match, EzyHashMap> {

    @Override
    public EzyHashMap write(EzyMarshaller ezyMarshaller, Match match) {
      val data = "{map: " + GsonHolder.getWithExcludeAnnotation().toJson(match) + "}";
      log.error(data);
      val map = GsonHolder.getNormalGson().fromJson(data, EzyHashMap.class);
      return map;
    }
  }
}
