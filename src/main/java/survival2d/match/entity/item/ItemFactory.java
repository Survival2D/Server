package survival2d.match.entity.item;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.RandomUtils;
import survival2d.match.config.GameConfig;
import survival2d.match.entity.base.Item;
import survival2d.match.type.GunType;
import survival2d.match.type.ItemType;

public class ItemFactory {

  public static Item create(ItemType itemType) {
    return switch (itemType) {
      case BULLET -> new BulletItem(randomGunType(), randomNumBullets());
      case HELMET -> new HelmetItem();
      case VEST -> new VestItem();
      case BANDAGE -> new BandageItem();
      case MEDKIT -> new MedKitItem();
    };
  }

  public static int randomNumBullets() {
    var numBlock = RandomUtils.nextInt(1, GameConfig.getInstance().getMaxBulletBlockPerItem());
    var numBullets = numBlock * GameConfig.getInstance().getBulletPerBlock();
    return numBullets;
  }

  public static GunType randomGunType() {
    var randomKey = RandomUtils.nextInt(0, GameConfig.getInstance().getMaxBulletRandomWeight());
    var gunType =
        GameConfig.getInstance().getBulletRandomWeights().ceilingEntry(randomKey).getValue();
    return gunType;
  }

  public static Item randomItem() {
    var randomKey = RandomUtils.nextInt(0, GameConfig.getInstance().getMaxItemRandomWeight());
    var itemType =
        GameConfig.getInstance().getItemRandomWeights().ceilingEntry(randomKey).getValue();
    return create(itemType);
  }

  public static List<Item> randomItems() {
    var numItems = RandomUtils.nextInt(1, GameConfig.getInstance().getNumMaxItemInContainer());
    return randomItems(numItems);
  }

  public static List<Item> randomItems(int numItems) {
    var items = new ArrayList<Item>();
    for (int i = 0; i < numItems; i++) {
      items.add(randomItem());
    }
    return items;
  }
}
