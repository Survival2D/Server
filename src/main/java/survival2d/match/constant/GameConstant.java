package survival2d.match.constant;

public class GameConstant {
  public static final int TICK_PER_SECOND = 60;
  public static final int PERIOD_PER_TICK = 1000 / TICK_PER_SECOND;
  public static final float INITIAL_BULLET_DISTANCE = 10;
  public static final float ITEM_ON_MAP_RADIUS = 35;
  public static final int TEAM_PLAYER = 4;
  public static final int HEADSHOT_DAMAGE = 3;
  public static final int BODY_DAMAGE = 1;
  public static final int PLAY_ZONE_TIME = 3 * 60 * TICK_PER_SECOND;
  public static final int MAX_OBJECT_SIZE = 200;
  public static final int DOUBLE_MAX_OBJECT_SIZE = 2 * MAX_OBJECT_SIZE;
  public static final int QUAD_MAX_OBJECT_SIZE = 2 * DOUBLE_MAX_OBJECT_SIZE;
  public static final int SAFE_ZONE_DAMAGE = 1;
  public static final int MAX_PLAYER_IN_TEAM = 4;
}
