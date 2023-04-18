package survival2d.ai.bot.branch;

import survival2d.ai.bot.Bot;
import survival2d.ai.bot.BotBehaviorNode;
import survival2d.ai.bot.leaf.FindSafePositionNode;
import survival2d.ai.btree.BTNode;
import survival2d.ai.btree.branch.BTSequenceNode;

import java.util.ArrayList;

public class HideEnemyNode extends BTSequenceNode {
    HideEnemyNode(Bot controller) {
        super();

        BotBehaviorNode find = new FindSafePositionNode();
        find.setController(controller);
        BTNode move = new MoveToSafePositionNode(controller);

        ArrayList<BTNode> children = new ArrayList<>();
        children.add(find);
        children.add(move);

        this.setChildren(children);
    }
}
