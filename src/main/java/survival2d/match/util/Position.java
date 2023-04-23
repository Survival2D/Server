package survival2d.match.util;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class Position {

  public static Position LEFT = new Position(-1, 0);
  public static Position RIGHT = new Position(1, 0);
  public static Position DOWN = new Position(0, -1);
  public static Position UP = new Position(0, 1);
  public static List<Position> NEIGHBOURS = List.of(LEFT, RIGHT, DOWN, UP);
  public int x;
  public int y;

  public Position(Position position) {
    this.x = position.x;
    this.y = position.y;
  }
}
