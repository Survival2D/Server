package survival2d.ai.btree;

public class BehaviorTree {
    private final BTNode root;

    BehaviorTree(BTNode root) {
        this.root = root;
    }

    public void processTree() {
        root.processNode();
    }
}
