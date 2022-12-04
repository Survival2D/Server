package survival2d.match.constant;

public class GameConstant {

  public static final float CLIENT_FIXED_DELTA_TIME = 0.025f;
  public static final int MAX_HISTORY_SIZE = 10;
  public static final float ATTACK_RANGE_UPPER_BOUND = 2.0f;
  public static final float HAMMER_DISTANCE_UPPER_BOUND = 2.0f;
  public static final float INITIAL_BULLET_DISTANCE = 10;
  public static final float ITEM_ON_MAP_RADIUS = 35;

  public static final int TEAM_PLAYER = 4;
  public static final int TICK_PER_SECOND = 60;
  public static final int PERIOD_PER_TICK = 1000 / TICK_PER_SECOND;
  public static final int HEADSHOT_DAMAGE = 3;
  public static final int BODY_DAMAGE = 1;
}
