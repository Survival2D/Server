package com.survival2d.server.network.match.response;

import com.survival2d.server.game.entity.Bullet;
import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;
import lombok.Builder;
import lombok.Data;
import org.locationtech.jts.math.Vector2D;

@Data
@Builder
@EzyObjectBinding
public class CreateBulletResponse {

  private Bullet bullet;
  private Vector2D direction;
}
