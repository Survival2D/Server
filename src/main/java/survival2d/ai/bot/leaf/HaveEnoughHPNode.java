package survival2d.ai.bot.leaf;

import survival2d.ai.bot.BotBehaviorNode;
import survival2d.match.config.GameConfig;

import java.util.concurrent.ThreadLocalRandom;

public class HaveEnoughHPNode extends BotBehaviorNode {
    @Override
    public void processNode() {
        double percent = this.controller.getConfidencePercent();
        double hp = this.controller.getPlayerInfo().getHp();

        double random = 1.0 + (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.2;
        double hpNeed = (1.0 - percent) * GameConfig.getInstance().getDefaultPlayerHp() * random;
        hpNeed = Math.min(hpNeed, GameConfig.getInstance().getDefaultPlayerHp());
        if (hp > hpNeed) success();
        else fail();
    }
}
