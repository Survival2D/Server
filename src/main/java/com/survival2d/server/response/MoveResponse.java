package com.survival2d.server.response;

import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;
import com.tvd12.gamebox.math.Vec3;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@EzyObjectBinding
public class MoveResponse {

  String name;
  Vec3 position;
}
