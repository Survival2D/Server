package survival2d.ai.btree.decorator;

import java.util.ArrayList;
import survival2d.ai.btree.BTNode;
import survival2d.ai.btree.BTValue;

public class BTUntilSuccessNode extends BTNode {
  public BTUntilSuccessNode(BTNode child) {
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
    BTValue value = child.getValue();
    if (value == BTValue.FAILURE || value == BTValue.RUNNING) {
      this.running();
    } else {
      success();
    }
  }
}
