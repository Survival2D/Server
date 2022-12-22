package survival2d.ai.bot;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import survival2d.ai.btree.BTNode;
import survival2d.ai.btree.BehaviorTree;
import survival2d.match.action.PlayerTakeItem;
import survival2d.match.config.GameConfig;
import survival2d.match.entity.item.ItemOnMap;
import survival2d.match.entity.match.Match;
import survival2d.match.entity.obstacle.Container;
import survival2d.match.entity.player.Player;
import survival2d.util.math.MathUtil;

import java.util.Collection;
import java.util.List;

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
    private Vector2D destPos = null;
    private List<Vector2D> path = null;

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
        targetPlayer = "";
        Collection<Player> players = this.match.getNearByPlayer(this.player.getPosition());
        for (Player player : players) {
            if (!player.getPlayerId().equals(controlId)) {
                targetPlayer = player.getPlayerId();
                destPos = player.getPosition();
                break;
            }
        }
        return !targetPlayer.equals("");
    }

    public Vector2D getEnemyPosition(String username) {
        return destPos;
    }

    public void commandFireEnemy() {
        destPos = this.getEnemyPosition(targetPlayer);
        this.commandMove();

        Vector2D attackDirection = destPos.subtract(this.player.getPosition());
        this.match.onPlayerAttack(controlId, attackDirection);
    }

    public boolean getNearbyCrate() {
        targetCreateId = -1;
        Collection<Container> crates = this.match.getNearByContainer(this.player.getPosition());
        for (Container crate : crates) {
            targetCreateId = crate.getId();
            destPos = crate.getPosition();
            return true;
        }
        return false;
    }

    public Vector2D getCratePosition(int id) {
        return destPos;
    }

    public void commandBreakCrate() {
        destPos = this.getCratePosition(targetCreateId);
        this.commandMove();

        Vector2D attackDirection = destPos.subtract(this.player.getPosition());
        this.match.onPlayerAttack(controlId, attackDirection);
    }

    public boolean getNearbyItem() {
        targetItemId = -1;
        Collection<ItemOnMap> items = this.match.getNearByItem(this.player.getPosition());
        for (ItemOnMap item : items) {
            targetItemId = item.getId();
            destPos = item.getPosition();
            return true;
        }
        return false;
    }

    public Vector2D getItemPosition(int id) {
        return destPos;
    }

    public void commandTakeItem() {
        destPos = this.getItemPosition(targetItemId);
        this.commandMove();

        Collection<ItemOnMap> items = this.match.getNearByItem(this.player.getPosition());
        for (ItemOnMap item : items) {
            if (MathUtil.isIntersect(item.getPosition(), item.getShape(), this.player.getPosition(), this.player.getShape())) {
                this.match.onReceivePlayerAction(controlId, new PlayerTakeItem());
                this.commandStopMove();
                break;
            }
        }
    }

    public boolean commandTakeNearbyItem() {
        Collection<ItemOnMap> items = this.match.getNearByItem(this.player.getPosition());
        for (ItemOnMap item : items) {
            if (MathUtil.isIntersect(item.getPosition(), item.getShape(), this.player.getPosition(), this.player.getShape())) {
                this.match.onReceivePlayerAction(controlId, new PlayerTakeItem());
                return true;
            }
        }
        return false;
    }

    public void commandMove() {
        if (destPos == null) return;

        if (this.path == null) {
            this.path = this.match.getPathFromTo(this.player.getPosition(), destPos);
        }

        if (this.path.isEmpty()) {
            this.commandStopMove();
        }
        else {
            Vector2D nextPosition = this.path.get(0);
            Vector2D moveVector = nextPosition.subtract(this.player.getPosition());
            this.match.onPlayerMove(controlId, moveVector, this.player.getRotation());
        }
    }

    private void commandStopMove() {
        destPos = null;
        this.path = null;
    }

    public boolean findSafePosition() {
        destPos = new Vector2D(GameConfig.getInstance().getMapWidth()/2, GameConfig.getInstance().getMapHeight()/2);
        return true;
    }

    public void commandMoveToSafePosition() {
        this.commandMove();
    }

    public void commandMoveToCenter() {
        destPos = new Vector2D(GameConfig.getInstance().getMapWidth()/2, GameConfig.getInstance().getMapHeight()/2);
        this.commandMove();
    }

    public void commandMoveRandom() {

    }

    public boolean isMoving() {
        return this.destPos != null;
    }
}
