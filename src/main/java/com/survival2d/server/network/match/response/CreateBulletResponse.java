package com.survival2d.server.network.match.response;

import com.survival2d.server.game.entity.Bullet;
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
public class CreateBulletResponse {

  private Bullet bullet;

  @EzyWriterImpl
  public static class CreateBulletResponseResponseWriter implements EzyWriter<CreateBulletResponse, EzyHashMap> {
    @Override
    public EzyHashMap write(EzyMarshaller ezyMarshaller, CreateBulletResponse response) {
      val data = "{map: " + GsonHolder.getNormalGson().toJson(response) + "}";
      val map = GsonHolder.getNormalGson().fromJson(data, EzyHashMap.class);
      return map;
    }
  }
}
