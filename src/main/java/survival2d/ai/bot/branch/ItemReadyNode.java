package survival2d.ai.bot.branch;

import survival2d.ai.bot.Bot;
import survival2d.ai.bot.BotBehaviorNode;
import survival2d.ai.bot.leaf.HaveArmorNode;
import survival2d.ai.bot.leaf.HaveEnoughBulletNode;
import survival2d.ai.bot.leaf.HaveGunNode;
import survival2d.ai.btree.branch.BTSequenceNode;

import java.util.ArrayList;

public class ItemReadyNode extends BTSequenceNode {

    public ItemReadyNode(Bot controller) {
        super();

        BotBehaviorNode haveGun = new HaveGunNode();
        BotBehaviorNode haveBullet = new HaveEnoughBulletNode();
        BotBehaviorNode haveArmor = new HaveArmorNode();

        ArrayList<BotBehaviorNode> children = new ArrayList<>();
        children.add(haveGun);
        children.add(haveBullet);
        children.add(haveArmor);

        children.forEach((BotBehaviorNode node) -> node.setController(controller));
    }
}
