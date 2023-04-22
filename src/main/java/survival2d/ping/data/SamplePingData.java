package survival2d.ping.data;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import survival2d.match.entity.Match;

public class SamplePingData {

  public static int userId = 1999;
  public static Vector2 position = new Vector2(MathUtils.PI, MathUtils.PI);
  public static float rotation = new Vector2(1, 1).angleRad();
  public static Match match;

  static {
    match = new Match(9999);
    for (int i = 0; i < 100; i++) {
      match.addPlayer(0, i);
    }
  }
}
