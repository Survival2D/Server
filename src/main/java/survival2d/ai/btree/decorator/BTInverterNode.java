package survival2d.ai.btree.decorator;

import survival2d.ai.btree.BTNode;

public class BTInverterNode extends BTNode {

    public BTInverterNode(BTNode child) {
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
        switch (child.getValue()) {
            case SUCCESS:
                this.fail();
                break;
            case FAILURE:
                this.success();
                break;
            case RUNNING:
                this.running();
                break;
            default:
                this.fail();
                break;
        }
    }
}
