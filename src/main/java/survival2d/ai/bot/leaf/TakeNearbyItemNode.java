package survival2d.ai.bot.leaf;

import survival2d.ai.bot.BotBehaviorNode;

public class TakeNearbyItemNode extends BotBehaviorNode {
  @Override
  public void processNode() {
    boolean canTake = this.controller.commandTakeNearbyItem();
    if (canTake) success();
    fail();
  }
}
