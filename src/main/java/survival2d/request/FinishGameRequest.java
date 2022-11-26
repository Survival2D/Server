package survival2d.request;

import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EzyObjectBinding
public class FinishGameRequest {

  private String gameName;
  private long gameId;
}
