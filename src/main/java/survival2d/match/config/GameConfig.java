package survival2d.match.config;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.Getter;
import survival2d.match.constant.GameConstant;
import survival2d.match.type.GunType;
import survival2d.match.type.HelmetType;
import survival2d.match.type.ItemType;
import survival2d.match.type.VestType;
import survival2d.util.config.ConfigReader;
import survival2d.util.serialize.GsonHolder;
import survival2d.util.serialize.PostProcessable;

@Getter
public class GameConfig implements PostProcessable {

  private static final String CONFIG_FILE = "config.json";
  @Getter private static GameConfig instance;

  static {
    load();
  }

  private float mapWidth;
  private float mapHeight;
  private List<Float> safeZonesRadius;
  private float defaultSafeZoneCenterX; // self-calculation
  private float defaultSafeZoneCenterY; // self-calculation
  private float defaultSafeZoneRadius; // self-calculation
  private int secondsPerSafeZone;
  private int ticksPerSafeZone; // self-calculation

  private float treeRootRadius;
  private float treeFoliageRadius;
  private double treeHp;
  private float containerSize;
  private double containerHp;
  private int numMaxItemInContainer;
  private float stoneRadius;
  private double stoneHp;
  private float wallSize;
  private float itemOnMapRadius;

  private double defaultPlayerHp;
  private float defaultPlayerSpeed;
  private float playerViewWidth;
  private float playerViewHeight;
  private float playerViewWidthPlus2; // self-calculation
  private float playerViewHeightPlus2; // self-calculation
  private double halfPlayerViewWidth; // self-calculation
  private double halfPlayerViewHeight; // self-calculation
  private float playerHeadRadius;
  private float playerBodyRadius;

  private Map<VestType, Double> vestReduceDamagePercent;
  private Map<HelmetType, Double> helmetReduceDamagePercent;
  private double bandageHeal;
  private double medKitHeal;
  private Map<ItemType, Integer> itemWeights;
  private int maxItemRandomWeight; // self-calculation
  private TreeMap<Integer, ItemType> itemRandomWeights; // self-calculation
  private Map<GunType, Integer> bulletWeights;
  private int maxBulletRandomWeight; // self-calculation
  private TreeMap<Integer, GunType> bulletRandomWeights; // self-calculation
  private int bulletPerBlock;
  private int maxBulletBlockPerItem;

  private WeaponConfig handConfig;
  private Map<GunType, GunConfig> gunConfigs;
  private float bulletSpeed;
  private float bulletDamageRadius;
  private float bulletRadius;
  private int defaultBullets;

  public static void load() {
    instance =
        ConfigReader.fromFile(CONFIG_FILE, GameConfig.class, GsonHolder.getEnablePostProcess());
  }

  @Override
  public void postProcess() {
    defaultSafeZoneCenterX = mapWidth / 2;
    defaultSafeZoneCenterY = mapHeight / 2;
    defaultSafeZoneRadius = mapWidth * (float) Math.sqrt(2);

    ticksPerSafeZone = secondsPerSafeZone * GameConstant.TICK_PER_SECOND;

    halfPlayerViewWidth = playerViewWidth / 2;
    halfPlayerViewHeight = playerViewHeight / 2;
    playerViewWidthPlus2 = playerViewWidth + 2;
    playerViewHeightPlus2 = playerViewHeight + 2;

    itemRandomWeights = new TreeMap<>();
    maxItemRandomWeight = 0;
    for (var entry : itemWeights.entrySet()) {
      maxItemRandomWeight += entry.getValue();
      itemRandomWeights.put(maxItemRandomWeight, entry.getKey());
    }

    bulletRandomWeights = new TreeMap<>();
    maxBulletRandomWeight = 0;
    for (var entry : bulletWeights.entrySet()) {
      maxBulletRandomWeight += entry.getValue();
      bulletRandomWeights.put(maxBulletRandomWeight, entry.getKey());
    }
  }
}
