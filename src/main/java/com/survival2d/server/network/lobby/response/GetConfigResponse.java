package com.survival2d.server.network.lobby.response;

import com.survival2d.server.config.MapConfig;
import com.survival2d.server.util.packet.AbstractResponseWriter;
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
