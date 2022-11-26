package survival2d.response;

import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@EzyObjectBinding
public class GetMMORoomResponse {

  List<String> players;
  String master;
}
