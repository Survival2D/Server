package survival2d.ai.bot.branch;

import java.util.ArrayList;
import survival2d.ai.bot.Bot;
import survival2d.ai.bot.BotBehaviorNode;
import survival2d.ai.bot.leaf.*;
import survival2d.ai.btree.BTNode;
import survival2d.ai.btree.branch.BTSequenceNode;

public class HealNode extends BTSequenceNode {
  HealNode(Bot controller) {
    super();

    BotBehaviorNode hpLost = new HPLostNode();
    hpLost.setController(controller);
    BotBehaviorNode haveMed = new HaveMedicineNode();
    haveMed.setController(controller);
    BotBehaviorNode useMed = new UseMedicineNode();
    useMed.setController(controller);

    ArrayList<BTNode> children = new ArrayList<>();
    children.add(hpLost);
    children.add(haveMed);
    children.add(useMed);

    this.setChildren(children);
  }
}
