package survival2d.ai.bot.leaf;

import survival2d.ai.bot.BotBehaviorNode;

public class HaveEnoughBulletNode extends BotBehaviorNode {
    @Override
    public void processNode() {
//        double percent = this.controller.getConfidencePercent();
//        int numBullet = this.controller.getPlayerInfo().getNumBullet();
//        int numBulletsNeed = (int) (1 - percent) * 100;
//        if (numBullet > numBulletsNeed) success();
//        else fail();

        success();
    }
}
