package survival2d.ai.bot.leaf;

import survival2d.ai.bot.BotBehaviorNode;
import survival2d.match.config.GameConfig;

public class HPLostNode extends BotBehaviorNode {
    @Override
    public void processNode() {
        double hp = this.controller.getPlayerInfo().getHp();
        if (hp < GameConfig.getInstance().getDefaultPlayerHp()) success();
        else fail();
    }
}
