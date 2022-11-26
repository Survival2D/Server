package survival2d.request;

import com.google.gson.Gson;
import com.tvd12.ezyfox.binding.EzyReader;
import com.tvd12.ezyfox.binding.EzyUnmarshaller;
import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;
import com.tvd12.ezyfox.binding.annotation.EzyReaderImpl;
import com.tvd12.gamebox.math.Vec2;
import lombok.Data;

@Data
@EzyObjectBinding
public class PlayerMoveRequest {

  Vec2 direction;

  @EzyReaderImpl
  public static class PlayerMoveRequestReader implements EzyReader<Object, PlayerMoveRequest> {

    @Override
    public PlayerMoveRequest read(EzyUnmarshaller ezyUnmarshaller, Object o) {
      return new Gson().fromJson(o.toString(), PlayerMoveRequest.class);
    }
  }
}
