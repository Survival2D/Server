package survival2d.ai.bot.leaf;

import survival2d.ai.bot.BotBehaviorNode;

public class TakeNearbyItemNode extends BotBehaviorNode {
    @Override
    public void processNode() {
        this.controller.commandTakeItem();
        //TODO: update value
        running();
    }
}
