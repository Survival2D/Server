package survival2d.ai.bot.branch;

import survival2d.ai.bot.Bot;
import survival2d.ai.bot.BotBehaviorNode;
import survival2d.ai.bot.leaf.EnemiesAroundNode;
import survival2d.ai.bot.leaf.MoveToDangerousAreaNode;
import survival2d.ai.btree.BTNode;
import survival2d.ai.btree.branch.BTSequenceNode;

import java.util.ArrayList;

public class FindEnemiesNode extends BTSequenceNode {
    FindEnemiesNode(Bot controller) {
        BotBehaviorNode move = new MoveToDangerousAreaNode();
        BotBehaviorNode enemiesAround = new EnemiesAroundNode();

        ArrayList<BTNode> children = new ArrayList<>();
        children.add(move);
        children.add(enemiesAround);

        this.setChildren(children);
    }
}
