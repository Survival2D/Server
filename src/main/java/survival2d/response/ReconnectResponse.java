package survival2d.response;

import survival2d.entity.GameState;
import survival2d.model.Position;
import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EzyObjectBinding
public class ReconnectResponse {

  private long gameId;
  private long playerScore;
  private GameState gameState = GameState.FINISHED;
  private List<GameObject> gameObjects = new ArrayList<>();

  public void addGameObject(GameObject gameObject) {
    this.gameObjects.add(gameObject);
  }

  @Getter
  @Setter
  @EzyObjectBinding
  @AllArgsConstructor
  @NoArgsConstructor
  public static class GameObject {

    private int id;
    private int type;
    private String name;
    private boolean visible;
    private Position position;
  }
}
