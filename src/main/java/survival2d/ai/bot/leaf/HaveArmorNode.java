package survival2d.ai.bot.leaf;

import survival2d.ai.bot.BotBehaviorNode;

public class HaveArmorNode extends BotBehaviorNode {
    @Override
    public void processNode() {
        double percent = this.getConfidencePercent();
        //TODO: check có giáp nón hay không (phần trăm tự tin)
    }
}
