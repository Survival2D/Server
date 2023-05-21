package survival2d.ai.bot.leaf;

import survival2d.ai.bot.BotBehaviorNode;

public class EnemiesAroundNode extends BotBehaviorNode {
  @Override
  public void processNode() {
    boolean bool = this.controller.getNearbyEnemy();
    if (bool) success();
    else fail();
  }
}
