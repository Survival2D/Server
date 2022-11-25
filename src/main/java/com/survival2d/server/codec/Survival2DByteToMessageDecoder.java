package com.survival2d.server.codec;

import com.survival2d.server.flatbuffers.Color;
import com.survival2d.server.flatbuffers.Equipment;
import com.survival2d.server.flatbuffers.Monster;
import com.survival2d.server.flatbuffers.Weapon;
import com.tvd12.ezyfox.codec.EzyMessageDeserializer;
import com.tvd12.ezyfox.codec.EzyStringToObjectDecoder;
import java.nio.ByteBuffer;

public class Survival2DByteToMessageDecoder implements EzyStringToObjectDecoder {

  private final EzyMessageDeserializer deserializer;

  public Survival2DByteToMessageDecoder(EzyMessageDeserializer deserializer) {
    this.deserializer = deserializer;
  }

  @Override
  public Object decode(String bytes) {
//    return decode(bytes.getBytes());
    return deserializer.deserialize(bytes);
  }

  @Override
  public Object decode(byte[] bytes) {
    ByteBuffer buf = ByteBuffer.wrap(bytes);
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
    return bytes;
  }
}
