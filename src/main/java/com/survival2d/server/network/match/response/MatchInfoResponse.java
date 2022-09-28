package com.survival2d.server.network.match.response;

import com.survival2d.server.game.entity.Match;
import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MatchInfoResponse {

  Match match;
}
