package survival2d.match.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AStarCell extends Point implements Comparable<AStarCell> {
  AStarCell parent;
  double g, h;

  public AStarCell(int x, int y) {
    super(x, y);
    g = h = Double.MAX_VALUE;
  }

  public double getF() {
    return g + h;
  }

  @Override
  public int compareTo(AStarCell o) {
    return Double.compare(getF(), o.getF());
  }
}
