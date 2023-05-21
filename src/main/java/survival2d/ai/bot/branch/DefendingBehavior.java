package survival2d.ai.bot.branch;

import java.util.ArrayList;
import survival2d.ai.bot.Bot;
import survival2d.ai.btree.BTNode;
import survival2d.ai.btree.branch.BTSequenceNode;
import survival2d.ai.btree.decorator.BTAlwaysSuccessNode;
import survival2d.ai.btree.decorator.BTUntilSuccessNode;

public class DefendingBehavior extends BTSequenceNode {
  public DefendingBehavior(Bot controller) {
    super();

    BTNode heal = new HealNode(controller);
    BTNode alwaysSuccess = new BTAlwaysSuccessNode(heal);
    BTNode ensureSafety = new EnsureSafetyNode(controller);
    BTNode findItems = new FindItemsNode(controller);
    BTNode untilSuccess = new BTUntilSuccessNode(findItems);
    BTNode takeItem = new TakeItemNode(controller);

    ArrayList<BTNode> children = new ArrayList<>();
    children.add(alwaysSuccess);
    children.add(ensureSafety);
    children.add(untilSuccess);
    children.add(takeItem);

    this.setChildren(children);
  }
}
