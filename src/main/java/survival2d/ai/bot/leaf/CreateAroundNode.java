package survival2d.ai.bot.leaf;

import survival2d.ai.bot.BotBehaviorNode;

public class CreateAroundNode extends BotBehaviorNode {
    @Override
    public void processNode() {
        boolean bool = this.controller.getNearbyCrate();
        if (bool) success();
        else fail();
    }
}
