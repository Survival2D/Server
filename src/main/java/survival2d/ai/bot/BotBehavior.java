package survival2d.ai.bot;

import java.util.ArrayList;
import survival2d.ai.bot.branch.AttackingBehavior;
import survival2d.ai.bot.branch.CounteringBehavior;
import survival2d.ai.bot.branch.DefendingBehavior;
import survival2d.ai.btree.BTNode;
import survival2d.ai.btree.branch.BTSelectorNode;

public class BotBehavior extends BTSelectorNode {
  public BotBehavior(Bot controller) {
    super();

    BTNode counter = new CounteringBehavior(controller);
    BTNode attack = new AttackingBehavior(controller);
    BTNode defend = new DefendingBehavior(controller);

    ArrayList<BTNode> children = new ArrayList<>();
    children.add(counter);
    children.add(attack);
    children.add(defend);

    this.setChildren(children);
  }
}
