package survival2d.ai.bot.leaf;

import survival2d.ai.bot.BotBehaviorNode;

public class ItemAroundNode extends BotBehaviorNode {
    @Override
    public void processNode() {
        boolean bool = this.controller.getNearbyItem();
        if (bool) success();
        else fail();
    }
}
