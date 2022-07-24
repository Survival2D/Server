package com.survival2d.server.request;

import com.survival2d.server.request.entity.Object;
import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EzyObjectBinding
public class TestRequest {

  List<Integer> arr;
  Object obj;
}
