package survival2d.ai.bot;

import survival2d.ai.btree.BTNode;
import survival2d.ai.btree.BehaviorTree;

public class Bot {
    static final double TICK_DELTA_TIME = 1.0;

    BehaviorTree behaviorTree;

    private double deltaTimeTick;
    private double confidencePercent;
    private String controlId;

    public Bot() {
        BTNode botBehavior = new BotBehavior(this);
        behaviorTree = new BehaviorTree(botBehavior);

        deltaTimeTick = 0.0;
        confidencePercent = 1.0;
        controlId = "";
    }

    public void setControlId(String id) {
        controlId = id;
    }

    public void setConfidencePercent(double confidencePercent) {
        this.confidencePercent = confidencePercent;
    }

    public void processBot(double dt) {
        deltaTimeTick += dt;
        if (deltaTimeTick >= TICK_DELTA_TIME) {
            deltaTimeTick = 0.0;
            this.behaviorTree.processTree();
        }
    }
}
