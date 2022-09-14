package com.survival2d.server.network.lobby.response;

import com.survival2d.server.network.lobby.entity.FindMatchResult;
import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@EzyObjectBinding
public class FindMatchResponse {

  FindMatchResult result;
  String matchId;
}
