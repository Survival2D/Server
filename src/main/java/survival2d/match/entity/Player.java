package survival2d.match.entity;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import survival2d.match.entity.base.Destroyable;
import survival2d.match.entity.base.HasHp;
import survival2d.match.entity.base.Item;
import survival2d.match.entity.config.BulletType;
import survival2d.match.entity.config.GunType;
import survival2d.match.entity.weapon.Gun;
import survival2d.match.entity.weapon.Hand;
import survival2d.util.serialize.ExcludeFromGson;

@Data
@Slf4j
public class Player implements Destroyable, HasHp {

  String playerId;
  Vector2 position = new Vector2();
  float rotation;
  @ExcludeFromGson
  double speed = 10;
  @ExcludeFromGson
  double hp = 100;
  @ExcludeFromGson
  Vector2 direction;
  @ExcludeFromGson
  List<Weapon> weapons = new ArrayList<>();
  @ExcludeFromGson
  Map<Item, Integer> items; // Map item to quantity
  @ExcludeFromGson
  Map<BulletType, Integer> bullets; // Map bullet to quantity
  @ExcludeFromGson
  int currentWeaponIndex;
  int team;
  @ExcludeFromGson
  Circle shape = new Circle(30);
  @ExcludeFromGson
  Circle head = new Circle(10);

  public Player(String playerId, int team) {
    this.playerId = playerId;
    this.team = team;
    this.weapons.add(new Hand());
    Gun gun = new Gun(GunType.NORMAL);
    gun.reload(100);
    this.weapons.add(gun);
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

  
  public void reduceHp(double damage) {
    hp -= damage;
  }

  
  public void reloadWeapon() {
    var optWeapon = getCurrentWeapon();
    if (!optWeapon.isPresent()) {
      log.warn("current weapon is not present");
      return;
    }
    var weapon = optWeapon.get();
    if (weapon instanceof Gun) {
      ((Gun) weapon).reload(100);
    }
  }
}
