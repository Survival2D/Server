package com.survival2d.server.network.lobby.response;

import com.survival2d.server.network.lobby.entity.ResponseError;
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
