package com.survival2d.server.network.match.response;

import com.survival2d.server.game.entity.ItemOnMap;
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
public class PlayerTakeItemResponse {
  private String username;

  private ItemOnMap item;

  @EzyWriterImpl
  public static class ResponseWriter implements EzyWriter<PlayerTakeItemResponse, EzyHashMap> {
    @Override
    public EzyHashMap write(EzyMarshaller ezyMarshaller, PlayerTakeItemResponse response) {
      val data = "{map: " + GsonHolder.getNormalGson().toJson(response) + "}";
      val map = GsonHolder.getNormalGson().fromJson(data, EzyHashMap.class);
      return map;
    }
  }
}
