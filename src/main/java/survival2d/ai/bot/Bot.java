package survival2d.ai.bot;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import survival2d.ai.btree.BTNode;
import survival2d.ai.btree.BehaviorTree;
import survival2d.match.action.PlayerChangeWeapon;
import survival2d.match.action.PlayerTakeItem;
import survival2d.match.config.GameConfig;
import survival2d.match.entity.base.MapObject;
import survival2d.match.entity.item.ItemOnMap;
import survival2d.match.entity.match.Match;
import survival2d.match.entity.obstacle.Container;
import survival2d.match.entity.player.Player;
import survival2d.util.math.MathUtil;

import java.util.Collection;
import java.util.List;

public class Bot {
    static final double NUM_TICK_CHANGE_STATUS = 60;

    BehaviorTree behaviorTree;

    private double deltaTick;
    private double confidencePercent;
    private String controlId;

    private Match match;
    private Player player = null;

    private Vector2D destPos = null;
    private Container destCrate = null;
    private Player destEnemy = null;
    private int destItemId = -1;
    private List<Vector2D> path = null;

    private BotBehaviorNode runningNode = null;

    public Bot() {
        BTNode botBehavior = new BotBehavior(this);
        behaviorTree = new BehaviorTree(botBehavior);

        deltaTick = 0;
        confidencePercent = 1.0;
        controlId = "";
    }

    public void setMatch(Match match, String id) {
        this.match = match;
        controlId = id;
        this.player = match.getPlayerInfo(controlId);

        this.match.onReceivePlayerAction(controlId, new PlayerChangeWeapon(1));
    }

    public void setConfidencePercent(double confidencePercent) {
        this.confidencePercent = confidencePercent;
    }

    public void setRunningNode(BotBehaviorNode runningNode) {
        this.runningNode = runningNode;
    }

    public void processBot() {
        if (this.player == null || this.player.isDestroyed()) return;

        deltaTick += 1;
        if (deltaTick >= NUM_TICK_CHANGE_STATUS) {
            deltaTick = 0;
            this.behaviorTree.processTree();
        }
        else {
            if (this.runningNode != null) {
                this.runningNode.processNode();
            }
        }
    }

    public Player getPlayerInfo() {
        return player;
    }

    public double getConfidencePercent() {
        return this.confidencePercent;
    }

    public boolean getNearbyEnemy() {
        if (destEnemy != null && !destEnemy.isDestroyed()) return true;
        destEnemy = null;
        Collection<Player> players = this.match.getNearByPlayer(this.player.getPosition());
        for (Player player : players) {
            if (!player.getPlayerId().equals(controlId)) {
                destPos = player.getPosition();
                destEnemy = player;
                return true;
            }
        }
        return false;
    }

    public boolean commandFireEnemy() {
        if (destEnemy.isDestroyed()) {
            destEnemy = null;
            this.commandStopMove();
            return false;
        }
        destPos = destEnemy.getPosition();
        this.commandMove();

        if (!this.isMoving()) {
            destEnemy = null;
            return false;
        }

        Vector2D attackDirection = destPos.subtract(this.player.getPosition());
        this.match.onPlayerAttack(controlId, attackDirection);

        System.out.println(controlId + "fire enemy");

        return true;
    }

    public boolean getNearbyCrate() {
        if (destCrate != null && !destCrate.isDestroyed()) return true;
        destCrate = null;
        Collection<Container> crates = this.match.getNearByContainer(this.player.getPosition());
        for (Container crate : crates) {
            destPos = crate.getPosition();
            destCrate = crate;
            return true;
        }
        return false;
    }

    public boolean commandBreakCrate() {
        if (destCrate.isDestroyed()) {
            destCrate = null;
            this.commandStopMove();
            return false;
        }
        destPos = destCrate.getPosition();
        this.commandMove();

        if (!this.isMoving()) {
            destCrate = null;
            return false;
        }

        Vector2D attackDirection = destPos.subtract(this.player.getPosition());
        this.match.onPlayerAttack(controlId, attackDirection);

        System.out.println(controlId + "break crate");

        return true;
    }

    public boolean getNearbyItem() {
        if (this.match.getObjectsById(destItemId) != null) return true;
        destItemId = -1;
        Collection<ItemOnMap> items = this.match.getNearByItem(this.player.getPosition());
        for (ItemOnMap item : items) {
            destPos = item.getPosition();
            destItemId = item.getId();
            return true;
        }
        return false;
    }

    public void commandTakeItem() {
        MapObject item = this.match.getObjectsById(destItemId);
        if (item == null) {
            this.commandStopMove();
            return;
        }

        System.out.println(controlId + "taking item");

        destPos = item.getPosition();
        this.commandMove();

        if (MathUtil.isIntersect(item.getPosition(), item.getShape(), this.player.getPosition(), this.player.getShape())) {
            this.match.onReceivePlayerAction(controlId, new PlayerTakeItem());
            this.commandStopMove();
            System.out.println(controlId + "taken item");
        }
    }

    public boolean commandTakeNearbyItem() {
        Collection<ItemOnMap> items = this.match.getNearByItem(this.player.getPosition());
        for (ItemOnMap item : items) {
            if (MathUtil.isIntersect(item.getPosition(), item.getShape(), this.player.getPosition(), this.player.getShape())) {
                this.match.onReceivePlayerAction(controlId, new PlayerTakeItem());
                System.out.println(controlId + "taken a nearby item");
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
            if (nextPosition.distance(this.player.getPosition()) <= GameConfig.getInstance().getDefaultPlayerSpeed()) {
                this.path.remove(0);
            }
            if (this.path.isEmpty()) {
                this.commandStopMove();
            }
            else {
                nextPosition = this.path.get(0);
                Vector2D moveVector = nextPosition.subtract(this.player.getPosition());
                this.match.onPlayerMove(controlId, moveVector, this.player.getRotation());
            }
        }
    }

    private void commandStopMove() {
        destPos = null;
        this.path = null;

        System.out.println(controlId + "stop move");
    }

    public boolean findSafePosition() {
        destPos = new Vector2D(GameConfig.getInstance().getMapWidth()/2, GameConfig.getInstance().getMapHeight()/2);
        return true;
    }

    public void commandMoveToSafePosition() {
        this.commandMove();

        System.out.println(controlId + "move to safe position");
    }

    public void commandMoveToCenter() {
        destPos = new Vector2D(GameConfig.getInstance().getMapWidth()/2, GameConfig.getInstance().getMapHeight()/2);
        this.commandMove();

        System.out.println(controlId + "move to center");
    }

    public void commandMoveRandom() {
        Vector2D dest = new Vector2D(GameConfig.getInstance().getMapWidth()/2, GameConfig.getInstance().getMapHeight()/2);
        Vector2D moveVector = dest.subtract(this.player.getPosition());
        this.match.onPlayerMove(controlId, moveVector, this.player.getRotation());

        System.out.println(controlId + "move random");
    }

    public boolean isMoving() {
        return this.destPos != null;
    }
}
