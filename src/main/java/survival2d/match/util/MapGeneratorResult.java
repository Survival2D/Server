package survival2d.match.util;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class MapGeneratorResult {
  int[][] tiles;
  List<Tile> mapObjects;

  public void printTiles() {
    for (int[] row : tiles) {
      for (int x : row) {
        System.out.print(x);
      }
      System.out.println();
    }
  }

  public void printWalls() {
    for (int[] row : tiles) {
      for (int x : row) {
        if (x == TileObject.WALL.ordinal()) {
          System.out.print("X");
        } else {
          System.out.print("-");
        }
      }
      System.out.println();
    }
  }
}
