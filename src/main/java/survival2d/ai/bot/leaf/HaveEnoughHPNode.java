package survival2d.ai.bot.leaf;

import survival2d.ai.bot.BotBehaviorNode;

public class HaveEnoughHPNode extends BotBehaviorNode {
    @Override
    public void processNode() {
        double percent = this.getConfidencePercent();
        //TODO: check có đủ máu hay không (phần trăm tự tin)
    }
}
