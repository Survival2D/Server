package com.survival2d.server.network.match.response;

import com.survival2d.server.game.entity.Match;
import com.survival2d.server.util.serialize.GsonHolder;
import com.tvd12.ezyfox.binding.EzyMarshaller;
import com.tvd12.ezyfox.binding.EzyWriter;
import com.tvd12.ezyfox.binding.annotation.EzyWriterImpl;
import com.tvd12.ezyfox.entity.EzyHashMap;
import lombok.Builder;
import lombok.Data;
import lombok.val;

@Data
@Builder
public class MatchInfoResponse {

  Match match;

  @EzyWriterImpl
  public static class MatchWriter implements EzyWriter<Match, EzyHashMap> {

    @Override
    public EzyHashMap write(EzyMarshaller ezyMarshaller, Match match) {
      val data = "{map: " + GsonHolder.getNormalGson().toJson(match) + "}";
      val map = GsonHolder.getNormalGson().fromJson(data, EzyHashMap.class);
      return map;
    }
  }
}
