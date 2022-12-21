package survival2d.ai.bot.branch;

import survival2d.ai.bot.Bot;
import survival2d.ai.bot.BotBehaviorNode;
import survival2d.ai.bot.leaf.HaveEnoughHPNode;
import survival2d.ai.bot.leaf.MoveWhileAttackingNode;
import survival2d.ai.btree.BTNode;
import survival2d.ai.btree.branch.BTSequenceNode;
import survival2d.ai.btree.decorator.BTAlwaysSuccessNode;

import java.util.ArrayList;

public class AttackNode extends BTSequenceNode {
    AttackNode(Bot controller) {
        super();

        BotBehaviorNode moveWhileAttacking = new MoveWhileAttackingNode();
        moveWhileAttacking.setController(controller);
        BTNode alwaysSuccess = new BTAlwaysSuccessNode(moveWhileAttacking);
        BotBehaviorNode haveEnoughHP = new HaveEnoughHPNode();
        haveEnoughHP.setController(controller);

        ArrayList<BTNode> children = new ArrayList<>();
        children.add(alwaysSuccess);
        children.add(haveEnoughHP);

        this.setChildren(children);
    }
}
