package survival2d.util.packet;

import survival2d.util.serialize.GsonHolder;
import com.tvd12.ezyfox.entity.EzyHashMap;
import lombok.val;

public class PacketUtil {

  public static EzyHashMap toEzyHashMap(Object obj) {
    val data = "{map:" + GsonHolder.getNormalGson().toJson(obj) + "}";
    return GsonHolder.getNormalGson().fromJson(data, EzyHashMap.class);
  }
}
