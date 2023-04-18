package survival2d.ai.btree.decorator;

import survival2d.ai.btree.BTNode;

import java.util.ArrayList;

public class BTAlwaysSuccessNode extends BTNode {

    public BTAlwaysSuccessNode(BTNode child) {
        this.children = new ArrayList<>();
        this.children.add(child);
    }

    @Override
    public void processNode() {
        if (this.children.isEmpty()) {
            this.fail();
            return;
        }

        BTNode child = this.children.get(0);
        child.processNode();
        this.success();
    }
}
