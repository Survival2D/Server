package survival2d.util.packet;

import com.tvd12.ezyfox.binding.EzyMarshaller;
import com.tvd12.ezyfox.binding.EzyWriter;
import com.tvd12.ezyfox.entity.EzyHashMap;

public abstract class AbstractResponseWriter<T> implements EzyWriter<T, EzyHashMap> {
  @Override
  public EzyHashMap write(EzyMarshaller ezyMarshaller, T response) {
    return PacketUtil.toEzyHashMap(response);
  }
}
