package survival2d.ai.bot.leaf;

import survival2d.ai.bot.BotBehaviorNode;

public class FireNode extends BotBehaviorNode {
  @Override
  public void processNode() {
    boolean bool = this.controller.commandFireEnemy();
    if (bool) success();
    else fail();
  }
}
