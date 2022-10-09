package com.survival2d.server.util;

import com.survival2d.server.util.serialize.GsonHolder;
import com.tvd12.ezyfox.entity.EzyHashMap;
import lombok.val;

public class PacketUtil {

  public EzyHashMap toEzyHashMap(Object obj) {
    val data = "{map:" + GsonHolder.getNormalGson().toJson(obj) + "}";
    return GsonHolder.getNormalGson().fromJson(data, EzyHashMap.class);
  }
}
