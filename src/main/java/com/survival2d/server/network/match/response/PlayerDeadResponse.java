package com.survival2d.server.network.match.response;

import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@EzyObjectBinding
public class PlayerDeadResponse {

  private String username;
}
