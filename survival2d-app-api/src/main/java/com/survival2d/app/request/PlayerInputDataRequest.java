package com.survival2d.app.request;

import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;
import lombok.Data;

@Data
@EzyObjectBinding
public class PlayerInputDataRequest {
	int t;
	boolean[] k;
	float[] r;
}
