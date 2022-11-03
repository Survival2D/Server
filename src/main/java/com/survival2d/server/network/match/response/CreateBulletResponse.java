package com.survival2d.server.network.match.response;

import com.survival2d.server.game.entity.Bullet;
import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@EzyObjectBinding
public class CreateBulletResponse {

  private Bullet bullet;
}
