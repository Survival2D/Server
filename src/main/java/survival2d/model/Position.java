package survival2d.model;

import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EzyObjectBinding
public class Position {

  private double x;
  private double y;
  private double z;
}
