package survival2d.match.entity.item;

import static survival2d.match.entity.config.ItemType.BACKPACK;
import static survival2d.match.entity.config.ItemType.BANDAGE;
import static survival2d.match.entity.config.ItemType.BULLET;
import static survival2d.match.entity.config.ItemType.HELMET;
import static survival2d.match.entity.config.ItemType.MEDKIT;
import static survival2d.match.entity.config.ItemType.VEST;
import static survival2d.match.entity.config.ItemType.WEAPON;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import survival2d.match.config.GameConfig;
import survival2d.match.entity.base.Item;
import survival2d.match.entity.config.BulletType;
import survival2d.match.entity.config.GunType;
import survival2d.match.entity.config.ItemType;

public class ItemFactory {
  private static final ItemType[] itemsToRandom =
      ArrayUtils.toArray(WEAPON, BULLET, BACKPACK, HELMET, VEST, BANDAGE, MEDKIT);

  public static Item create(ItemType itemType) {
    switch (itemType) {
      case WEAPON:
        return new GunItem(GunType.NORMAL, 10);
      case BULLET:
        return new BulletItem(BulletType.NORMAL, 10);
      case BACKPACK:
        return new BackPackItem();
      case HELMET:
        return new HelmetItem();
      case VEST:
        return new VestItem();
      case BANDAGE:
        return new BandageItem();
      case MEDKIT:
        return new MedKitItem();
    }
    return null;
  }

  public static Item randomItem() {
    val itemType = itemsToRandom[ThreadLocalRandom.current().nextInt(itemsToRandom.length)];
    return create(itemType);
  }

  public static List<Item> randomItems() {
    val numItems =
        ThreadLocalRandom.current().nextInt(1, GameConfig.getInstance().getNumMaxItemInContainer());
    return randomItems(numItems);
  }

  public static List<Item> randomItems(int numItems) {
    val items = new ArrayList<Item>();
    for (int i = 0; i < numItems; i++) {
      items.add(randomItem());
    }
    return items;
  }
}
