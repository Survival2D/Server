package survival2d.ai.bot.branch;

import survival2d.ai.bot.Bot;
import survival2d.ai.bot.BotBehaviorNode;
import survival2d.ai.bot.leaf.HaveArmorNode;
import survival2d.ai.bot.leaf.HaveEnoughBulletNode;
import survival2d.ai.bot.leaf.HaveGunNode;
import survival2d.ai.btree.BTNode;
import survival2d.ai.btree.branch.BTSequenceNode;

import java.util.ArrayList;

public class ItemReadyNode extends BTSequenceNode {

    public ItemReadyNode(Bot controller) {
        super();

        BotBehaviorNode haveGun = new HaveGunNode();
        haveGun.setController(controller);
        BotBehaviorNode haveBullet = new HaveEnoughBulletNode();
        haveBullet.setController(controller);
        BotBehaviorNode haveArmor = new HaveArmorNode();
        haveArmor.setController(controller);

        ArrayList<BTNode> children = new ArrayList<>();
        children.add(haveGun);
        children.add(haveBullet);
        children.add(haveArmor);

        this.setChildren(children);
    }
}
