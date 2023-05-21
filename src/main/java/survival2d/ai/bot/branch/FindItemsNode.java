package survival2d.ai.bot.branch;

import java.util.ArrayList;
import survival2d.ai.bot.Bot;
import survival2d.ai.bot.BotBehaviorNode;
import survival2d.ai.bot.leaf.ItemAroundNode;
import survival2d.ai.bot.leaf.MoveAroundNode;
import survival2d.ai.btree.BTNode;
import survival2d.ai.btree.branch.BTSelectorNode;

public class FindItemsNode extends BTSelectorNode {
  FindItemsNode(Bot controller) {
    super();

    BotBehaviorNode itemAround = new ItemAroundNode();
    itemAround.setController(controller);
    BTNode breakCrate = new BreakCreatesNode(controller);
    BotBehaviorNode move = new MoveAroundNode();
    move.setController(controller);

    ArrayList<BTNode> children = new ArrayList<>();
    children.add(itemAround);
    children.add(breakCrate);
    children.add(move);

    this.setChildren(children);
  }
}
