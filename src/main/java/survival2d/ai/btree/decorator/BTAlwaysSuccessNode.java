package survival2d.ai.btree.decorator;

import survival2d.ai.btree.BTNode;

public class BTAlwaysSuccessNode extends BTNode {

    BTAlwaysSuccessNode(BTNode child) {
        this.children.clear();
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
