package survival2d.match.entity.item;

import org.junit.jupiter.api.Test;

class ItemFactoryTest {

  @Test
  void createItem() {
    for (int i = 0; i < 100; i++) System.out.println(ItemFactory.randomItem());
  }
}
