package com.survival2d.server.request;

import com.survival2d.server.request.entity.Object;
import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EzyObjectBinding
public class TestRequest {

  int[] arr;
  Object obj;
}
