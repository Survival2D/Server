package survival2d.ai.bot.branch;

import survival2d.ai.bot.Bot;
import survival2d.ai.bot.BotBehaviorNode;
import survival2d.ai.bot.leaf.HaveEnoughHPNode;
import survival2d.ai.btree.BTNode;
import survival2d.ai.btree.branch.BTSequenceNode;

import java.util.ArrayList;

public class ReadyToFightNode extends BTSequenceNode {
    ReadyToFightNode(Bot controller) {
        super();

        BTNode itemReady = new ItemReadyNode(controller);
        BotBehaviorNode haveEnoughHP = new HaveEnoughHPNode();
        haveEnoughHP.setController(controller);

        ArrayList<BTNode> children = new ArrayList<>();
        children.add(itemReady);
        children.add(haveEnoughHP);

        this.setChildren(children);
    }
}
