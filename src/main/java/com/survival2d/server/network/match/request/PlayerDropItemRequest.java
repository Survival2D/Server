package com.survival2d.server.network.match.request;

import com.survival2d.server.util.serialize.GsonHolder;
import com.tvd12.ezyfox.binding.EzyReader;
import com.tvd12.ezyfox.binding.EzyUnmarshaller;
import com.tvd12.ezyfox.binding.annotation.EzyReaderImpl;
import lombok.Data;


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
