package com.survival2d.server.game.entity;

import com.survival2d.server.game.entity.config.GunType;
import com.survival2d.server.game.entity.weapon.Gun;
import com.survival2d.server.game.entity.weapon.Hand;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Data;
import org.locationtech.jts.math.Vector2D;

@Data
public class PlayerImpl implements Player {

  String playerId;
  Vector2D position =
      new Vector2D(
          ThreadLocalRandom.current().nextDouble() * 100,
          ThreadLocalRandom.current().nextDouble() * 100);
  PlayerState state;
  double rotation;
  double speed = 10;
  Vector2D direction;
  List<Weapon> weapons = new ArrayList<>();
  int currentWeaponIndex;
  long team;

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
    if (index > 0 && index < weapons.size()) {
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


}
