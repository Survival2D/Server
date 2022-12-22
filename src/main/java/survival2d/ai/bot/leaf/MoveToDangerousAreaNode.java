package survival2d.ai.bot.leaf;

import survival2d.ai.bot.BotBehaviorNode;

public class MoveToDangerousAreaNode extends BotBehaviorNode {
    @Override
    public void processNode() {
        this.controller.commandMoveToCenter();
        if (this.controller.isMoving()) {
            running();
            this.controller.setRunningNode(this);
        }
        else {
            success();
            this.controller.setRunningNode(null);
        }
    }
}
