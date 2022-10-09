package com.survival2d.server.network.match.request;

import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;
import lombok.Data;


@Data
@EzyObjectBinding
public class PlayerChangeWeaponRequest {

  private int slot;
}
