package survival2d.ai.bot.leaf;

import survival2d.ai.bot.BotBehaviorNode;
import survival2d.match.entity.player.Player;

public class UseMedicineNode extends BotBehaviorNode {
    @Override
    public void processNode() {
        Player player = this.controller.getPlayerInfo();
        if (player.useBandage()) success();
        else if (player.useMedKit()) success();
        else fail();
    }
}
