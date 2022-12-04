package survival2d.util.packet;

import com.tvd12.ezyfox.binding.EzyReader;
import com.tvd12.ezyfox.binding.EzyUnmarshaller;
import com.tvd12.ezyfox.binding.annotation.EzyReaderImpl;
import com.tvd12.ezyfox.entity.EzyHashMap;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.locationtech.jts.math.Vector2D;
import survival2d.util.serialize.GsonHolder;

public class PacketUtil {

  public static EzyHashMap toEzyHashMap(Object obj) {
    val data = "{map:" + GsonHolder.getNormalGson().toJson(obj) + "}";
    return GsonHolder.getNormalGson().fromJson(data, EzyHashMap.class);
  }

  @EzyReaderImpl
  @Slf4j
  public static class Vector2DReader implements EzyReader<Object, Vector2D> {
    @Override
    public Vector2D read(EzyUnmarshaller ezyUnmarshaller, Object o) {
      return GsonHolder.getNormalGson().fromJson(o.toString(), Vector2D.class);
    }
  }
}
