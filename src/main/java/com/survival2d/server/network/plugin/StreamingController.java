package com.survival2d.server.network.plugin;

import com.survival2d.server.flatbuffers.Color;
import com.survival2d.server.flatbuffers.Equipment;
import com.survival2d.server.flatbuffers.Monster;
import com.survival2d.server.flatbuffers.Weapon;
import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyfoxserver.context.EzyZoneContext;
import com.tvd12.ezyfoxserver.controller.EzyAbstractZoneEventController;
import com.tvd12.ezyfoxserver.event.EzyStreamingEvent;
import java.nio.ByteBuffer;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@EzySingleton
//@EzyEventHandler(STREAMING)
@Slf4j
public class StreamingController extends EzyAbstractZoneEventController<EzyStreamingEvent> {

  @Override
  public void handle(EzyZoneContext ezyZoneContext, EzyStreamingEvent ezyStreamingEvent) {
    val raw = Arrays.copyOf(ezyStreamingEvent.getBytes(), ezyStreamingEvent.getBytes().length);
    val buf = ByteBuffer.wrap(ezyStreamingEvent.getBytes());
    Monster monster = Monster.getRootAsMonster(buf);

    // Note: We did not set the `mana` field explicitly, so we get back the default value.
    assert monster.mana() == (short) 150;
    assert monster.hp() == (short) 300;
    assert monster.name().equals("Orc");
    assert monster.color() == Color.Red;
    assert monster.pos().x() == 1.0f;
    assert monster.pos().y() == 2.0f;
    assert monster.pos().z() == 3.0f;

    // Get and test the `inventory` FlatBuffer `vector`.
    for (int i = 0; i < monster.inventoryLength(); i++) {
      assert monster.inventory(i) == (byte) i;
    }

    // Get and test the `weapons` FlatBuffer `vector` of `table`s.
    String[] expectedWeaponNames = {"Sword", "Axe"};
    int[] expectedWeaponDamages = {3, 5};
    for (int i = 0; i < monster.weaponsLength(); i++) {
      assert monster.weapons(i).name().equals(expectedWeaponNames[i]);
      assert monster.weapons(i).damage() == expectedWeaponDamages[i];
    }

    Weapon.Vector weaponsVector = monster.weaponsVector();
    for (int i = 0; i < weaponsVector.length(); i++) {
      assert weaponsVector.get(i).name().equals(expectedWeaponNames[i]);
      assert weaponsVector.get(i).damage() == expectedWeaponDamages[i];
    }

    // Get and test the `equipped` FlatBuffer `union`.
    assert monster.equippedType() == Equipment.Weapon;
    Weapon equipped = (Weapon) monster.equipped(new Weapon());
    assert equipped.name().equals("Axe");
    assert equipped.damage() == 5;
    System.out.println("The FlatBuffer was successfully created and verified!");
    log.info("raw {}", raw);
    ezyZoneContext.stream(raw, ezyStreamingEvent.getSession());
  }
}
