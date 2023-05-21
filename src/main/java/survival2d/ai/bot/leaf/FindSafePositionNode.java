package survival2d.ai.bot.leaf;

import survival2d.ai.bot.BotBehaviorNode;

public class FindSafePositionNode extends BotBehaviorNode {
  @Override
  public void processNode() {
    boolean bool = this.controller.findSafePosition();
    if (bool) success();
    else fail();
  }
}
