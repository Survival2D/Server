package com.survival2d.server.network.match.response;

import com.survival2d.server.game.entity.math.Vector;
import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@EzyObjectBinding
public class PlayerMoveResponse {

  private String username;
  private Vector position;
  private double rotation;
}
