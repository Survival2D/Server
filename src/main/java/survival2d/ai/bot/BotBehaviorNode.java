package survival2d.ai.bot;

import survival2d.ai.btree.BTNode;

public abstract class BotBehaviorNode extends BTNode {
    protected Bot controller;

    public void setController(Bot controller) {
        this.controller = controller;
    }

    protected void getMatchInfo() {}
    protected void playerInControlInfo() {}
    protected double getConfidencePercent() {
        return 1.0;
    }
}
