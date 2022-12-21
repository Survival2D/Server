package survival2d.ai.bot;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import survival2d.ai.btree.BTNode;

public abstract class BotBehaviorNode extends BTNode {
    protected Bot controller;

    public void setController(Bot controller) {
        this.controller = controller;
    }

    protected void playerInControlInfo() {
        //TODO: get player info để check các trạng thái của player
    }

    protected double getConfidencePercent() {
        return this.controller.getConfidencePercent();
    }

    protected int getNearbyCrate() {
        //TODO: tìm thùng gần nhất để tới phá lấy item, return id thùng
        return 0;
    }

    protected Vector2D getCratePosition(int id) {
        //TODO: lấy vị trí thùng by id
        return null;
    }

    protected void commandMoveTo(Vector2D destPosition) {
        //TODO: ra lệnh di chuyển tới vị trí đích
    }

    protected void commandStopMove() {
        //TODO: ra lệnh dừng di chuyển
    }

    protected int commandAttackCrate(int id) {
        //TODO: ra lệnh tấn công thùng by id, return hp của thùng, số âm nếu không tấn công được
        return -1;
    }
}
