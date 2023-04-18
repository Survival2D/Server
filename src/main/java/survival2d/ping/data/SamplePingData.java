package survival2d.ping.data;



import survival2d.match.entity.Match;

public class SamplePingData {

  public static String username = "player_9999";
  public static Vector2 position = new Vector2(Math.sqrt(2), Math.sqrt(2));
  public static double rotation = 1 / Math.sqrt(2);
  public static Match match;

  static {
    try {
      match = new Match(9999);
      for (int i = 0; i < 100; i++) {
        match.addPlayer(0, "player_" + i);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
