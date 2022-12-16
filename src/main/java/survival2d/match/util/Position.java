package survival2d.match.util;

import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class Position {

  public static List<Position> _4_NEIGHBOURS =
      Arrays.asList(
          new Position(-1, 0), new Position(1, 0), new Position(0, -1), new Position(0, 1));
  int x;
  int y;

  public Position(Position position) {
    this.x = position.x;
    this.y = position.y;
  }
}
