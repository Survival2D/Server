package survival2d.ai.btree;

import java.util.ArrayList;

public abstract class BTNode {
    private BTValue value;
    protected ArrayList<BTNode> children;

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public BTValue getValue() {
        return value;
    }

    public void setChildren(ArrayList<BTNode> children) {
        this.children = children;
    }

    protected void success() {
        value = BTValue.SUCCESS;
    }

    protected void fail() {
        value = BTValue.FAILURE;
    }

    protected void running() {
        value = BTValue.RUNNING;
    }

    public abstract void processNode();
}
