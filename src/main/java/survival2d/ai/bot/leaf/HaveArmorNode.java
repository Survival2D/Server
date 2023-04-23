package survival2d.ai.bot.leaf;

import survival2d.ai.bot.BotBehaviorNode;
import survival2d.match.type.HelmetType;
import survival2d.match.type.VestType;

public class HaveArmorNode extends BotBehaviorNode {
    @Override
    public void processNode() {
        double percent = this.controller.getConfidencePercent();
        Player player = this.controller.getPlayerInfo();

        if (percent > 0.67) {
            success();
        }
        else if (percent > 0.33) {
            if (player.getHelmetType() == HelmetType.LEVEL_1 || player.getVestType() == VestType.LEVEL_1) success();
            else fail();
        }
        else {
            if (player.getHelmetType() == HelmetType.LEVEL_1 && player.getVestType() == VestType.LEVEL_1) success();
            else fail();
        }
    }
}
