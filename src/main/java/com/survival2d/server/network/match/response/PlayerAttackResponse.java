package com.survival2d.server.network.match.response;

import com.survival2d.server.game.entity.Weapon;
import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;
import lombok.Builder;
import lombok.Data;
import org.locationtech.jts.math.Vector2D;


@Data
@Builder
@EzyObjectBinding
public class PlayerAttackResponse {

  private String username;
  private Weapon weapon;
  private Vector2D position;
}
