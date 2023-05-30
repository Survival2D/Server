package survival2d.match.entity.player;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import survival2d.match.config.GameConfig;
import survival2d.match.entity.base.HasHp;
import survival2d.match.entity.base.Item;
import survival2d.match.entity.base.Movable;
import survival2d.match.entity.item.BulletItem;
import survival2d.match.entity.item.HelmetItem;
import survival2d.match.entity.item.VestItem;
import survival2d.match.entity.quadtree.BaseMapObject;
import survival2d.match.entity.weapon.Gun;
import survival2d.match.entity.weapon.Hand;
import survival2d.match.entity.weapon.Weapon;
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
  int id; // mapObjectId
  int playerId; // userId của player
  int team;
  float rotation;
  @GsonTransient float speed = GameConfig.getInstance().getDefaultPlayerSpeed();
  @GsonTransient double hp = GameConfig.getInstance().getDefaultPlayerHp();
  @GsonTransient List<Weapon> weapons = new ArrayList<>();
  @GsonTransient HelmetType helmetType = HelmetType.LEVEL_0;
  @GsonTransient VestType vestType = VestType.LEVEL_0;
  @GsonTransient Map<GunType, Integer> bullets = new HashMap<>(); // Map bullet to quantity
  @GsonTransient Map<ItemType, Integer> items = new HashMap<>(); // Chỉ map những item 1 loại
  @GsonTransient int currentWeaponIndex;
  @GsonTransient Circle body = new Circle(0, 0, GameConfig.getInstance().getPlayerBodyRadius());
  @GsonTransient Circle head = new Circle(0, 0, GameConfig.getInstance().getPlayerHeadRadius());

  public Player(int playerId, int team) {
    this.playerId = playerId;
    this.team = team;
    position = MatchUtil.randomPosition(100, 900, 100, 900);
    weapons.add(new Hand());
    weapons.add(new Gun(GunType.PISTOL));
    weapons.add(new Gun(GunType.SHOTGUN));
    weapons.add(new Gun(GunType.SNIPER));
    for (var gunType: GunType.values()) {
      bullets.put(gunType, GameConfig.getInstance().getDefaultBullets());
    }
  }

  @Override
  public void setPosition(Vector2 position) {
    this.position = position;
    body.setPosition(position);
    head.setPosition(position);
  }

  public Vector2 getAttackDirection() {
    return new Vector2(MathUtils.cos(rotation), MathUtils.sin(rotation)).nor();
  }

  private boolean isValidWeaponIndex(int index) {
    return index >= 0 && index < weapons.size();
  }

  public void switchWeapon(int index) {
    if (isValidWeaponIndex(index)) {
      currentWeaponIndex = index;
    }
  }

  public Weapon getCurrentWeapon() {
    if (!isValidWeaponIndex(currentWeaponIndex)) {
      return null;
    }
    return weapons.get(currentWeaponIndex);
  }

  public void reloadWeapon() {
    var weapon = getCurrentWeapon();
    if (weapon == null) {
      log.error("current weapon is null");
      return;
    }
    if (weapon instanceof Gun gun) {
      var gunType = gun.getType();
      var numBullets = bullets.getOrDefault(gunType, 0);
      var numLoadedBullets = gun.reload(numBullets);
      bullets.merge(gunType, -numLoadedBullets, Integer::sum);
    }
  }

  public void takeItem(Item item) {
    switch (item.getItemType()) {
      case BULLET -> {
        var bulletItem = (BulletItem) item;
        takeBullet(bulletItem.getGunType(), bulletItem.getNumBullet());
      }
      case HELMET -> {
        var helmetItem = (HelmetItem) item;
        takeHelmet(helmetItem.getHelmetType());
      }
      case VEST -> {
        var vestItem = (VestItem) item;
        takeVest(vestItem.getVestType());
      }
      case MEDKIT -> takeMedKit();
      case BANDAGE -> takeBandage();
    }
  }

  private void takeBullet(GunType type, int numBullet) {
    bullets.merge(type, numBullet, Integer::sum);
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

  public int getNumItem(ItemType itemType) {
    return items.getOrDefault(itemType, 0);
  }

  public int getNumBullet(GunType type) {
    return bullets.getOrDefault(type, 0);
  }

  public Gun getGun(GunType type) {
    for (var weapon: weapons) {
      if (weapon instanceof Gun gun && gun.getType() == type) {
        return gun;
      }
    }
    return null;
  }

  private void heal(double amount) {
    hp = Math.min(hp + amount, GameConfig.getInstance().getDefaultPlayerHp());
  }

  public Rectangle getPlayerView() {
    var width = GameConfig.getInstance().getPlayerViewWidth();
    var height = GameConfig.getInstance().getPlayerViewHeight();
    return new Rectangle(position.x - width / 2, position.y - height / 2, width, height);
  }

  @Override
  public Shape2D getShape() {
    return body;
  }
}
