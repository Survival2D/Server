package survival2d.match.util;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@ToString
public class Point {
  public static final Point S = new Point(0, -1);
  public static final Point N = new Point(0, 1);
  public static final Point E = new Point(1, 0);
  public static final Point W = new Point(-1, 0);
  public static final Point SE = new Point(1, -1);
  public static final Point SW = new Point(-1, -1);
  public static final Point NE = new Point(1, 1);
  public static final Point NW = new Point(-1, 1);
  public static final List<Point> EIGHT_NEIGHBOURS =
      Collections.unmodifiableList(Lists.newArrayList(SW, W, NW, N, NE, E, SE, S));
  public static final List<Point> FOUR_NEIGHBOURS =
      Collections.unmodifiableList(Lists.newArrayList(W, N, E, S));
  public int x, y;

  public Point add(Point point) {
    return new Point(x + point.x, y + point.y);
  }
}
