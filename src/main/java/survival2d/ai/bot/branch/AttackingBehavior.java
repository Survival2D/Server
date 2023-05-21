package survival2d.ai.bot.branch;

import java.util.ArrayList;
import survival2d.ai.bot.Bot;
import survival2d.ai.btree.BTNode;
import survival2d.ai.btree.branch.BTSequenceNode;
import survival2d.ai.btree.decorator.BTAlwaysSuccessNode;
import survival2d.ai.btree.decorator.BTUntilSuccessNode;

public class AttackingBehavior extends BTSequenceNode {
  public AttackingBehavior(Bot controller) {
    super();

    BTNode readyToFight = new ReadyToFightNode(controller);
    BTNode heal = new HealNode(controller);
    BTNode alwaysSuccess = new BTAlwaysSuccessNode(heal);
    BTNode findEnemies = new FindEnemiesNode(controller);
    BTNode untilSuccess = new BTUntilSuccessNode(findEnemies);
    BTNode attack = new AttackNode(controller);

    ArrayList<BTNode> children = new ArrayList<>();
    children.add(readyToFight);
    children.add(alwaysSuccess);
    children.add(untilSuccess);
    children.add(attack);

    this.setChildren(children);
  }
}
