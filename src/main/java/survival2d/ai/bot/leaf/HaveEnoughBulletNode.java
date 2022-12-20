package survival2d.ai.bot.leaf;

import survival2d.ai.bot.BotBehaviorNode;

public class HaveEnoughBulletNode extends BotBehaviorNode {
    @Override
    public void processNode() {
        double percent = this.getConfidencePercent();
        //TODO: check đủ đạn để chiến đấu không (tính dựa trên phần trăm tự tin của bot)
    }
}
