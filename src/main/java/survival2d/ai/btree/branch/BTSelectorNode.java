package survival2d.ai.btree.branch;

import survival2d.ai.btree.BTNode;
import survival2d.ai.btree.BTValue;

import java.util.ArrayList;

public class BTSelectorNode extends BTNode {

    public BTSelectorNode() {
        this.children = new ArrayList<>();
    }

    public BTSelectorNode(ArrayList<BTNode> children) {
        this.setChildren(children);
    }

    @Override
    public void processNode() {
        for (BTNode child : this.children) {
            child.processNode();
            BTValue value = child.getValue();
            if (value == BTValue.SUCCESS) {
                this.success();
                break;
            }
            if (value == BTValue.RUNNING) {
                this.running();
                break;
            }
            if (value == BTValue.FAILURE) {
                this.fail();
            }
        }
    }
}
