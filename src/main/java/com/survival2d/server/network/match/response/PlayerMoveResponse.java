package com.survival2d.server.network.match.response;

import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

@Data
@Builder
@EzyObjectBinding
public class PlayerMoveResponse {

  private String username;
  private Vector2D position;
  private double rotation;
}
