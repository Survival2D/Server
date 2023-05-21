package survival2d.ai.bot.branch;

import java.util.ArrayList;
import survival2d.ai.bot.Bot;
import survival2d.ai.bot.BotBehaviorNode;
import survival2d.ai.bot.leaf.EnemiesAroundNode;
import survival2d.ai.btree.BTNode;
import survival2d.ai.btree.branch.BTSelectorNode;
import survival2d.ai.btree.decorator.BTInverterNode;

public class EnsureSafetyNode extends BTSelectorNode {
  EnsureSafetyNode(Bot controller) {
    super();

    BotBehaviorNode enemiesAround = new EnemiesAroundNode();
    enemiesAround.setController(controller);
    BTNode inverter = new BTInverterNode(enemiesAround);
    BTNode hide = new HideEnemyNode(controller);

    ArrayList<BTNode> children = new ArrayList<>();
    children.add(inverter);
    children.add(hide);

    this.setChildren(children);
  }
}
