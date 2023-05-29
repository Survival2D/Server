package survival2d.ai.bot.branch;

import java.util.ArrayList;
import survival2d.ai.bot.Bot;
import survival2d.ai.bot.BotBehaviorNode;
import survival2d.ai.bot.leaf.HaveArmorNode;
import survival2d.ai.bot.leaf.HaveEnoughBulletNode;
import survival2d.ai.btree.BTNode;
import survival2d.ai.btree.branch.BTSequenceNode;

public class ItemReadyNode extends BTSequenceNode {

  public ItemReadyNode(Bot controller) {
    super();

    BotBehaviorNode haveBullet = new HaveEnoughBulletNode();
    haveBullet.setController(controller);
    BotBehaviorNode haveArmor = new HaveArmorNode();
    haveArmor.setController(controller);

    ArrayList<BTNode> children = new ArrayList<>();
    children.add(haveBullet);
    children.add(haveArmor);

    this.setChildren(children);
  }
}
