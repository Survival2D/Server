package survival2d.match.entity.player;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import survival2d.match.config.GameConfig;
import survival2d.match.entity.base.Destroyable;
import survival2d.match.entity.base.HasHp;
import survival2d.match.entity.base.Item;
import survival2d.match.entity.base.Movable;
import survival2d.match.entity.item.BackPackItem;
import survival2d.match.entity.item.BulletItem;
import survival2d.match.entity.item.HelmetItem;
import survival2d.match.entity.item.VestItem;
import survival2d.match.entity.quadtree.BaseMapObject;
import survival2d.match.entity.quadtree.RectangleBoundary;
import survival2d.match.entity.weapon.Gun;
import survival2d.match.entity.weapon.Hand;
import survival2d.match.entity.weapon.Weapon;
import survival2d.match.type.BackPackType;
import survival2d.match.type.BulletType;
import survival2d.match.type.GunType;
import survival2d.match.type.HelmetType;
import survival2d.match.type.ItemType;
import survival2d.match.type.VestType;
import survival2d.match.util.MatchUtil;
import survival2d.util.serialize.GsonTransient;

@Getter
@Setter
@Slf4j
public class Player extends BaseMapObject implements Movable, HasHp {

  public static final int BODY_RADIUS = 30;
  public static final Circle BODY_SHAPE = new Circle(BODY_RADIUS);
  public static final Circle HEAD_SHAPE = new Circle(10);
  int id; // Id trên map
  int playerId; // Username của player
  Vector2 position = MatchUtil.randomPosition(100, 900, 100, 900);
  float rotation;
  @GsonTransient float speed = GameConfig.getInstance().getDefaultPlayerSpeed();
  @GsonTransient double hp = GameConfig.getInstance().getDefaultPlayerHp();
  @GsonTransient Vector2 direction;
  @GsonTransient List<Weapon> weapons = new ArrayList<>();
  @GsonTransient BackPackType backPackType = BackPackType.LEVEL_0;
  @GsonTransient HelmetType helmetType = HelmetType.LEVEL_0;
  @GsonTransient VestType vestType = VestType.LEVEL_0;
  @GsonTransient Map<BulletType, Integer> bullets = new HashMap<>(); // Map bullet to quantity
  @GsonTransient Map<ItemType, Integer> items = new HashMap<>(); // Chỉ map những item 1 loại
  @GsonTransient int currentWeaponIndex;
  int team;
  @GsonTransient Circle head = HEAD_SHAPE;

  public Player(int playerId, int team) {
    super(BODY_SHAPE);
    this.playerId = playerId;
    this.team = team;
    weapons.add(new Hand());
    Gun gun = new Gun(GunType.NORMAL);
    //    gun.reload(100);
    weapons.add(gun);
  }

  public Vector2 getAttackDirection() {
    return new Vector2(MathUtils.cos(rotation), MathUtils.sin(rotation));
  }

  public void switchWeapon(int index) {
    if (index >= 0 && index < weapons.size()) {
      currentWeaponIndex = index;
    }
  }

  public Optional<Weapon> getCurrentWeapon() {
    if (currentWeaponIndex < 0 || currentWeaponIndex >= weapons.size()) {
      return Optional.empty();
    }
    return Optional.of(weapons.get(currentWeaponIndex));
  }

  public void reloadWeapon() {
    val optWeapon = getCurrentWeapon();
    if (!optWeapon.isPresent()) {
      log.warn("current weapon is not present");
      return;
    }
    val weapon = optWeapon.get();
    if (weapon instanceof Gun) {
      val gun = (Gun) weapon;
      val gunType = gun.getType();
      val bulletType = gunType.getBulletType();
      gun.reload(bullets.getOrDefault(bulletType, 0));
    }
  }

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

  public boolean useMedKit() {
    if (items.get(ItemType.MEDKIT) == null || items.get(ItemType.MEDKIT) <= 0) {
      return false;
    }
    items.put(ItemType.MEDKIT, items.get(ItemType.MEDKIT) - 1);
    heal(GameConfig.getInstance().getMedKitHeal());
    return true;
  }

  public boolean useBandage() {
    if (items.get(ItemType.BANDAGE) == null || items.get(ItemType.BANDAGE) <= 0) {
      return false;
    }
    items.put(ItemType.BANDAGE, items.get(ItemType.BANDAGE) - 1);
    heal(GameConfig.getInstance().getBandageHeal());
    return true;
  }

  public int getNumItem(int itemId) {
    return items.getOrDefault(ItemType.parse(itemId), 0);
  }

  public int getNumBullet() {
    if (bullets.isEmpty()) return 0;
    return bullets.get(BulletType.NORMAL);
  }

  private void heal(double amount) {
    hp = Math.min(hp + amount, GameConfig.getInstance().getDefaultPlayerHp());
  }

  public RectangleBoundary getPlayerView() {
    val width = GameConfig.getInstance().getPlayerViewWidth();
    val height = GameConfig.getInstance().getPlayerViewHeight();
    return new RectangleBoundary(
        position.getX() - width / 2, position.getY() - height / 2, width, height);
  }

  public Gun getGun() {
    return (Gun) weapons.get(1);
  }
}
