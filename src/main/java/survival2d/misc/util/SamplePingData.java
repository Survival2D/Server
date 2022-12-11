package survival2d.misc.util;

import org.locationtech.jts.math.Vector2D;
import survival2d.match.entity.MatchImpl;

public class SamplePingData {
  public static String username = "player_9999";
  public static Vector2D position = new Vector2D(Math.sqrt(2), Math.sqrt(2));
  public static double rotation = 1 / Math.sqrt(2);
  public static MatchImpl match = new MatchImpl(9999);
  static {
    for (int i = 0; i < 100; i++) {
      match.addPlayer(0, "player_" + i);
    };
  }
}
