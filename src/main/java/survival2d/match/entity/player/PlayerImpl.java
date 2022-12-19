package survival2d.match.entity.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import survival2d.match.config.GameConfig;
import survival2d.match.entity.base.Circle;
import survival2d.match.entity.base.Item;
import survival2d.match.entity.config.BackPackType;
import survival2d.match.entity.config.BulletType;
import survival2d.match.entity.config.GunType;
import survival2d.match.entity.config.HelmetType;
import survival2d.match.entity.config.ItemType;
import survival2d.match.entity.config.VestType;
import survival2d.match.entity.item.BackPackItem;
import survival2d.match.entity.item.BulletItem;
import survival2d.match.entity.item.HelmetItem;
import survival2d.match.entity.item.VestItem;
import survival2d.match.entity.quadtree.BaseMapObject;
import survival2d.match.entity.quadtree.RectangleBoundary;
import survival2d.match.entity.weapon.Gun;
import survival2d.match.entity.weapon.Hand;
import survival2d.match.entity.weapon.Weapon;
import survival2d.util.serialize.ExcludeFromGson;

@Getter
@Setter
@Slf4j
public class PlayerImpl extends BaseMapObject implements Player {

  public static final int BODY_RADIUS = 30;
  public static final Circle BODY_SHAPE = new Circle(BODY_RADIUS);
  public static final Circle HEAD_SHAPE = new Circle(10);
  int id; // Id trên map
  String playerId; // Username của player
  Vector2D position =
      new Vector2D(RandomUtils.nextDouble(100, 900), RandomUtils.nextDouble(100, 900));
  double rotation;
  @ExcludeFromGson double speed = GameConfig.getInstance().getDefaultPlayerSpeed();
  @ExcludeFromGson double hp = GameConfig.getInstance().getDefaultPlayerHp();
  @ExcludeFromGson Vector2D direction;
  @ExcludeFromGson List<Weapon> weapons = new ArrayList<>();
  @ExcludeFromGson BackPackType backPackType = BackPackType.LEVEL_0;
  @ExcludeFromGson HelmetType helmetType = HelmetType.LEVEL_0;
  @ExcludeFromGson VestType vestType = VestType.LEVEL_0;
  @ExcludeFromGson Map<BulletType, Integer> bullets = new HashMap<>(); // Map bullet to quantity
  @ExcludeFromGson Map<ItemType, Integer> items = new HashMap<>(); // Chỉ map những item 1 loại
  @ExcludeFromGson int currentWeaponIndex;
  int team;
  @ExcludeFromGson Circle head = HEAD_SHAPE;

  public PlayerImpl(String playerId, int team) {
    super(BODY_SHAPE);
    this.playerId = playerId;
    this.team = team;
    weapons.add(new Hand());
    Gun gun = new Gun(GunType.NORMAL);
    gun.reload(100);
    weapons.add(gun);
  }

  @Override
  public Vector2D getAttackDirection() {
    return new Vector2D(Math.cos(rotation), Math.sin(rotation));
  }

  @Override
  public void switchWeapon(int index) {
    if (index >= 0 && index < weapons.size()) {
      currentWeaponIndex = index;
    }
  }

  @Override
  public Optional<Weapon> getCurrentWeapon() {
    if (currentWeaponIndex < 0 || currentWeaponIndex >= weapons.size()) {
      return Optional.empty();
    }
    return Optional.of(weapons.get(currentWeaponIndex));
  }

  @Override
  public void reloadWeapon() {
    val optWeapon = getCurrentWeapon();
    if (!optWeapon.isPresent()) {
      log.warn("current weapon is not present");
      return;
    }
    val weapon = optWeapon.get();
    if (weapon instanceof Gun) {
      ((Gun) weapon).reload(100);
    }
  }

  @Override
  public void takeItem(Item item) {
    switch (item.getItemType()) {
      case WEAPON:
        // TODO:
        break;
      case BACKPACK:
        val backPackItem = (BackPackItem) item;
        takeBackPack(backPackItem.getBackPackType());
        break;
      case BULLET:
        val bulletItem = (BulletItem) item;
        takeBullet(bulletItem.getBulletType(), bulletItem.getNumBullet());
        break;
      case HELMET:
        val helmetItem = (HelmetItem) item;
        takeHelmet(helmetItem.getHelmetType());
        break;
      case VEST:
        val vestItem = (VestItem) item;
        takeVest(vestItem.getVestType());
        break;
      case MEDKIT:
        takeMedKit();
        break;
      case BANDAGE:
        takeBandage();
        break;
    }
  }

  private void takeBackPack(BackPackType backPackType) {
    if (backPackType.compareTo(this.backPackType) > 0) {
      this.backPackType = backPackType;
    }
  }

  private void takeBullet(BulletType bulletType, int numBullet) {
    bullets.merge(bulletType, numBullet, Integer::sum);
  }

  private void takeHelmet(HelmetType helmetType) {
    if (helmetType.compareTo(this.helmetType) > 0) {
      this.helmetType = helmetType;
    }
  }

  private void takeVest(VestType vestType) {
    if (vestType.compareTo(this.vestType) > 0) {
      this.vestType = vestType;
    }
  }

  private void takeMedKit() {
    items.merge(ItemType.MEDKIT, 1, Integer::sum);
  }

  private void takeBandage() {
    items.merge(ItemType.BANDAGE, 1, Integer::sum);
  }

  @Override
  public boolean useMedKit() {
    if (items.get(ItemType.MEDKIT) == null || items.get(ItemType.MEDKIT) <= 0) {
      return false;
    }
    items.put(ItemType.MEDKIT, items.get(ItemType.MEDKIT) - 1);
    heal(GameConfig.getInstance().getMedKitHeal());
    return true;
  }

  @Override
  public boolean useBandage() {
    if (items.get(ItemType.BANDAGE) == null || items.get(ItemType.BANDAGE) <= 0) {
      return false;
    }
    items.put(ItemType.BANDAGE, items.get(ItemType.BANDAGE) - 1);
    heal(GameConfig.getInstance().getBandageHeal());
    return true;
  }

  @Override
  public int getNumItem(int itemId) {
    return items.getOrDefault(ItemType.parse(itemId), 0);
  }

  private void heal(double amount) {
    hp = Math.min(hp + amount, GameConfig.getInstance().getDefaultPlayerHp());
  }

  @Override
  public RectangleBoundary getPlayerView() {
    val width = GameConfig.getInstance().getPlayerViewWidth();
    val height = GameConfig.getInstance().getPlayerViewHeight();
    return new RectangleBoundary(
        position.getX() - width / 2, position.getY() - height / 2, width, height);
  }
}
