package survival2d.ai.btree.branch;

import java.util.ArrayList;
import survival2d.ai.btree.BTNode;
import survival2d.ai.btree.BTValue;

public class BTSequenceNode extends BTNode {

  public BTSequenceNode() {
    this.children = new ArrayList<>();
  }

  public BTSequenceNode(ArrayList<BTNode> children) {
    this.setChildren(children);
  }

  @Override
  public void processNode() {
    for (BTNode child : this.children) {
      child.processNode();
      BTValue value = child.getValue();
      if (value == BTValue.FAILURE) {
        this.fail();
        break;
      }
      if (value == BTValue.RUNNING) {
        this.running();
        break;
      }
      if (value == BTValue.SUCCESS) {
        this.success();
      }
    }
  }
}
