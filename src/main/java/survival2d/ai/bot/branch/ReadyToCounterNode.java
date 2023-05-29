package survival2d.ai.bot.branch;

import java.util.ArrayList;
import survival2d.ai.bot.Bot;
import survival2d.ai.bot.BotBehaviorNode;
import survival2d.ai.bot.leaf.HaveEnoughBulletNode;
import survival2d.ai.btree.BTNode;
import survival2d.ai.btree.branch.BTSequenceNode;

public class ReadyToCounterNode extends BTSequenceNode {
  ReadyToCounterNode(Bot controller) {
    super();

    BotBehaviorNode haveBullet = new HaveEnoughBulletNode();
    haveBullet.setController(controller);

    ArrayList<BTNode> children = new ArrayList<>();
    children.add(haveBullet);

    this.setChildren(children);
  }
}
