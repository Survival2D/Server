package survival2d.lobby.network.response;

import survival2d.match.config.MapConfig;
import survival2d.util.packet.AbstractResponseWriter;
import com.tvd12.ezyfox.binding.annotation.EzyWriterImpl;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetConfigResponse {

  MapConfig map;

  @EzyWriterImpl
  public static class Writer extends AbstractResponseWriter<GetConfigResponse> {

  }
}
