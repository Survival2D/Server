package survival2d.ai.bot.branch;

import java.util.ArrayList;
import survival2d.ai.bot.Bot;
import survival2d.ai.bot.BotBehaviorNode;
import survival2d.ai.bot.leaf.AttackCrateNode;
import survival2d.ai.bot.leaf.CreateAroundNode;
import survival2d.ai.btree.BTNode;
import survival2d.ai.btree.branch.BTSequenceNode;

public class BreakCreatesNode extends BTSequenceNode {
  BreakCreatesNode(Bot controller) {
    super();

    BotBehaviorNode crateAround = new CreateAroundNode();
    crateAround.setController(controller);
    BotBehaviorNode attackCrate = new AttackCrateNode();
    attackCrate.setController(controller);

    ArrayList<BTNode> children = new ArrayList<>();
    children.add(crateAround);
    children.add(attackCrate);

    this.setChildren(children);
  }
}
