package survival2d.match.config;

import java.util.List;
import lombok.Getter;
import survival2d.match.constant.GameConstant;
import survival2d.util.config.ConfigReader;
import survival2d.util.serialize.GsonHolder;
import survival2d.util.serialize.PostProcessable;

@Getter
public class GameConfig implements PostProcessable {

  private static final String CONFIG_FILE = "config.json";
  private double mapWidth;
  private double mapHeight;
  private List<Double> safeZonesRadius;
  private int minutePerSafeZone;
  private double defaultSafeZoneCenterX;
  private double defaultSafeZoneCenterY;
  private double defaultSafeZoneRadius;
  private int ticksPerSafeZone;
  private double defaultPlayerHp;
  private double defaultPlayerSpeed;
  private double bandageHeal;
  private double medKitHeal;
  private double playerViewWidth;
  private double playerViewHeight;
  private double playerViewWidthPlus1;
  private double playerViewHeightPlus1;
  private double playerViewWidthPlus2;
  private double playerViewHeightPlus2;
  private double halfPlayerViewWidth;
  private double halfPlayerViewHeight;

  public static void load() {
    InstanceHolder.instance =
        ConfigReader.fromFile(CONFIG_FILE, GameConfig.class, GsonHolder.getEnablePostProcess());
  }

  public static GameConfig getInstance() {
    return InstanceHolder.instance;
  }

  @Override
  public void postProcess() {
    defaultSafeZoneCenterX = mapWidth / 2;
    defaultSafeZoneCenterY = mapHeight / 2;
    defaultSafeZoneRadius = mapWidth;
    ticksPerSafeZone = minutePerSafeZone * 60 * GameConstant.TICK_PER_SECOND;
    medKitHeal = defaultPlayerHp;

    halfPlayerViewWidth = playerViewWidth / 2;
    halfPlayerViewHeight = playerViewHeight / 2;
    playerViewWidthPlus1 = playerViewWidth + 1;
    playerViewHeightPlus1 = playerViewHeight + 1;
    playerViewWidthPlus2 = playerViewWidth + 2;
    playerViewHeightPlus2 = playerViewHeight + 2;
  }

  private static class InstanceHolder {

    private static GameConfig instance;

    static {
      load();
    }
  }
}
