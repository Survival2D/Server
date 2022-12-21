package survival2d.ai.bot;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import survival2d.ai.btree.BTNode;
import survival2d.ai.btree.BehaviorTree;
import survival2d.match.entity.match.Match;
import survival2d.match.entity.player.Player;

public class Bot {
    static final double TICK_DELTA_TIME = 1.0;

    BehaviorTree behaviorTree;

    private double deltaTimeTick;
    private double confidencePercent;
    private String controlId;

    private Match match;
    private Player player;

    private String targetPlayer = "";
    private int targetCreateId = -1;
    private int targetItemId = -1;
    private Vector2D destPos = new Vector2D(0, 0);

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

    public void setMatch(Match match) {
        this.match = match;
        this.player = match.getPlayerInfo(controlId);
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

    public Player getPlayerInfo() {
        return player;
    }

    public double getConfidencePercent() {
        return this.confidencePercent;
    }

    public boolean getNearbyEnemy() {
        //TODO: tìm địch gần nhất để tới phá lấy item, return id địch
        targetPlayer = "";
        return false;
    }

    public Vector2D getEnemyPosition(String username) {
        //TODO: lấy vị trí dich by id
        return null;
    }

    public void commandFireEnemy() {
        Vector2D pos = this.getEnemyPosition(targetPlayer);
        this.commandMoveTo(pos);
        //TODO: bắn địch thủ (đã có target)
    }

    public boolean getNearbyCrate() {
        //TODO: tìm thùng gần nhất để tới phá lấy item, return id thùng
        targetCreateId = -1;
        return false;
    }

    public Vector2D getCratePosition(int id) {
        //TODO: lấy vị trí thùng by id
        return null;
    }

    public void commandBreakCrate() {
        Vector2D pos = this.getCratePosition(targetCreateId);
        this.commandMoveTo(pos);
        //TODO: bắn thùng (đã có target)
    }

    public boolean getNearbyItem() {
        //TODO: tìm item gần nhất, return id item
        targetItemId = -1;
        return false;
    }

    public Vector2D getItemPosition(int id) {
        //TODO: lấy vị trí item by id
        return null;
    }

    public void commandTakeItem() {
        Vector2D pos = this.getCratePosition(targetItemId);
        this.commandMoveTo(pos);
        //TODO: nhặt item (đã có target)
    }

    public void commandMoveTo(Vector2D destPosition) {
        //TODO: ra lệnh di chuyển tới vị trí đích
    }

    public void commandStopMove() {
        //TODO: ra lệnh dừng di chuyển
    }

    public boolean findSafePosition() {
        //TODO: tìm vị trí an toàn
        destPos = new Vector2D(0, 0);
        return false;
    }

    public void commandMoveToSafePosition() {
        this.commandMoveTo(destPos);
    }

    public void commandMoveToCenter() {

    }

    public void commandMoveRandom() {

    }
}
