package survival2d.ai.bot.leaf;

import survival2d.ai.bot.BotBehaviorNode;

public class AttackCrateNode extends BotBehaviorNode {
    @Override
    public void processNode() {
        this.controller.commandBreakCrate();
        //TODO: update value
        success();
    }
}
