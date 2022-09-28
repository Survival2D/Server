package com.survival2d.server.request;

import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EzyObjectBinding
public class DeleteGameObjectRequest {

  private String gameName;
  private long gameId;
  private int objectId;
}
