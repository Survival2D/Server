package com.survival2d.server.network.match.response;

import com.survival2d.server.game.entity.Weapon;
import com.survival2d.server.util.serialize.GsonHolder;
import com.tvd12.ezyfox.binding.EzyMarshaller;
import com.tvd12.ezyfox.binding.EzyWriter;
import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;
import com.tvd12.ezyfox.entity.EzyHashMap;
import lombok.Builder;
import lombok.Data;
import lombok.val;
import org.locationtech.jts.math.Vector2D;


@Data
@Builder
@EzyObjectBinding
public class PlayerAttackResponse {

  private String username;
  private Weapon weapon;
  private Vector2D direction;
}
