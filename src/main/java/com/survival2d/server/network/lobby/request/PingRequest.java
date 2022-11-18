package com.survival2d.server.network.lobby.request;

import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;
import lombok.Data;

@Data
@EzyObjectBinding
public class PingRequest {
  long time;
}
