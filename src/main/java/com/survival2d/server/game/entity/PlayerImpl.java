package com.survival2d.server.game.entity;

import com.survival2d.server.game.entity.config.GunType;
import com.survival2d.server.game.entity.weapon.Gun;
import com.survival2d.server.game.entity.weapon.Hand;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Data;
import org.apache.commons.lang3.RandomUtils;
import org.locationtech.jts.math.Vector2D;

@Data
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
  int currentWeaponIndex;
  long team;
  double size = 30;

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
}
