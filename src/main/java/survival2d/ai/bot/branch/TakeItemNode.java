package survival2d.ai.bot.branch;

import java.util.ArrayList;
import survival2d.ai.bot.Bot;
import survival2d.ai.bot.BotBehaviorNode;
import survival2d.ai.bot.leaf.EquipItemNode;
import survival2d.ai.bot.leaf.MoveToItemNode;
import survival2d.ai.bot.leaf.PickUpItemNode;
import survival2d.ai.btree.BTNode;
import survival2d.ai.btree.branch.BTSequenceNode;
import survival2d.ai.btree.decorator.BTAlwaysSuccessNode;

public class TakeItemNode extends BTSequenceNode {
  TakeItemNode(Bot controller) {
    super();

    BotBehaviorNode move = new MoveToItemNode();
    move.setController(controller);
    BotBehaviorNode pick = new PickUpItemNode();
    pick.setController(controller);
    BotBehaviorNode equip = new EquipItemNode();
    equip.setController(controller);
    BTNode alwaysSuccess = new BTAlwaysSuccessNode(equip);

    ArrayList<BTNode> children = new ArrayList<>();
    children.add(move);
    children.add(pick);
    children.add(alwaysSuccess);

    this.setChildren(children);
  }
}
