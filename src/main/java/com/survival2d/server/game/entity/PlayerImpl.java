package com.survival2d.server.game.entity;

import com.survival2d.server.game.entity.base.Circle;
import com.survival2d.server.game.entity.base.Item;
import com.survival2d.server.game.entity.config.GunType;
import com.survival2d.server.game.entity.weapon.Gun;
import com.survival2d.server.game.entity.weapon.Hand;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.RandomUtils;
import org.locationtech.jts.math.Vector2D;

@Data
@Slf4j
public class PlayerImpl implements Player {

  String playerId;
  Vector2D position =
      new Vector2D(RandomUtils.nextDouble(100, 900), RandomUtils.nextDouble(100, 900));
  PlayerState state;
  double rotation;
  double speed = 10;
  double healthPoint = 100;
  Vector2D direction;
  List<Weapon> weapons = new ArrayList<>();
  Map<Item, Integer> items; //Map item to quantity
  int currentWeaponIndex;
  long team;
  double size = 30;
  Circle shape = new Circle(10);

  public PlayerImpl(String playerId, long team) {
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
  public void takeDamage(double damage) {
    healthPoint -= damage;
    if (healthPoint <= 0) {
      state = PlayerState.DEAD;
    }
  }

  @Override
  public boolean isDead() {
    return state == PlayerState.DEAD;
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
