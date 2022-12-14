package survival2d.match.entity.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import survival2d.match.entity.base.Circle;
import survival2d.match.entity.base.Item;
import survival2d.match.entity.config.BulletType;
import survival2d.match.entity.config.GunType;
import survival2d.match.entity.weapon.Gun;
import survival2d.match.entity.weapon.Hand;
import survival2d.match.entity.weapon.Weapon;
import survival2d.util.serialize.ExcludeFromGson;

@Data
@Slf4j
public class PlayerImpl implements Player {

  String playerId;
  Vector2D position =
      new Vector2D(RandomUtils.nextDouble(100, 900), RandomUtils.nextDouble(100, 900));
  double rotation;
  @ExcludeFromGson double speed = 10;
  @ExcludeFromGson double hp = 100;
  @ExcludeFromGson Vector2D direction;
  @ExcludeFromGson List<Weapon> weapons = new ArrayList<>();
  @ExcludeFromGson Map<Item, Integer> items; // Map item to quantity
  @ExcludeFromGson Map<BulletType, Integer> bullets; // Map bullet to quantity
  @ExcludeFromGson int currentWeaponIndex;
  int team;
  @ExcludeFromGson Circle shape = new Circle(30);
  @ExcludeFromGson Circle head = new Circle(10);

  public PlayerImpl(String playerId, int team) {
    this.playerId = playerId;
    this.team = team;
    this.weapons.add(new Hand());
    Gun gun = new Gun(GunType.NORMAL);
    gun.reload(100);
    this.weapons.add(gun);
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
  public void reduceHp(double damage) {
    hp -= damage;
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
}
