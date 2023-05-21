package survival2d.ai.bot.branch;

import java.util.ArrayList;
import survival2d.ai.bot.Bot;
import survival2d.ai.bot.BotBehaviorNode;
import survival2d.ai.bot.leaf.EnemiesAroundNode;
import survival2d.ai.btree.BTNode;
import survival2d.ai.btree.branch.BTSequenceNode;
import survival2d.ai.btree.decorator.BTAlwaysSuccessNode;

public class CounteringBehavior extends BTSequenceNode {
  public CounteringBehavior(Bot controller) {
    super();

    BotBehaviorNode enemiesAround = new EnemiesAroundNode();
    enemiesAround.setController(controller);
    BTNode heal = new HealNode(controller);
    BTNode alwaysSuccess = new BTAlwaysSuccessNode(heal);
    BTNode readyToCounter = new ReadyToCounterNode(controller);
    BTNode attack = new AttackNode(controller);

    ArrayList<BTNode> children = new ArrayList<>();
    children.add(enemiesAround);
    children.add(alwaysSuccess);
    children.add(readyToCounter);
    children.add(attack);

    this.setChildren(children);
  }
}
