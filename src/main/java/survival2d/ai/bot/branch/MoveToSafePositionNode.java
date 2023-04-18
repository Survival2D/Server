package survival2d.ai.bot.branch;

import survival2d.ai.bot.Bot;
import survival2d.ai.bot.BotBehaviorNode;
import survival2d.ai.bot.leaf.MoveToPositionNode;
import survival2d.ai.bot.leaf.TakeNearbyItemNode;
import survival2d.ai.btree.BTNode;
import survival2d.ai.btree.branch.BTSequenceNode;
import survival2d.ai.btree.decorator.BTAlwaysSuccessNode;

import java.util.ArrayList;

public class MoveToSafePositionNode extends BTSequenceNode {
    MoveToSafePositionNode(Bot controller) {
        super();

        BotBehaviorNode takeItem = new TakeNearbyItemNode();
        takeItem.setController(controller);
        BTNode alwaysSuccess = new BTAlwaysSuccessNode(takeItem);
        BotBehaviorNode move = new MoveToPositionNode();
        move.setController(controller);

        ArrayList<BTNode> children = new ArrayList<>();
        children.add(alwaysSuccess);
        children.add(move);

        this.setChildren(children);
    }
}
