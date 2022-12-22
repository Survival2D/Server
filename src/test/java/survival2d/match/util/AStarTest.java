package survival2d.match.util;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.val;
import org.junit.jupiter.api.Test;
import survival2d.match.util.AStar.Point;

class AStarTest {
  @Test
  public void testAStar() {
    val tiles = MapGenerator.generateMap().getTiles();
    val mapHeight = MapGenerator.MAP_HEIGHT;
    val mapWidth = MapGenerator.MAP_WIDTH;
    val aStar = new AStar(mapHeight, mapWidth, tiles);
    val result = aStar.aStarSearch(new Point(0, 0), new Point(mapWidth - 1, mapHeight - 1));
    printResult(tiles, result);
  }

  private void printResult(int[][] tiles, List<Point> result) {
    for (int i = 0; i < tiles.length; i++) {
      for (int j = 0; j < tiles[i].length; j++) {
        if (result.contains(new Point(i, j))) System.out.print("O");
        else {
          if (isUnBlocked(tiles, i, j)) {
            System.out.print("-");
          } else {
            System.out.print("X");
          }
        }
      }
      System.out.println();
    }
  }

  public Point randomUnblockedPosition(int[][] tiles, int row, int col) {
    val random = ThreadLocalRandom.current();
    val x = random.nextInt(col);
    val y = random.nextInt(row);
    if (isUnBlocked(tiles, x, y)) {
      return new Point(x, y);
    } else {
      return randomUnblockedPosition(tiles, row, col);
    }
  }

  protected boolean isUnBlocked(int[][] tiles, int x, int y) {
    return tiles[x][y] == TileObject.EMPTY.ordinal()
        || tiles[x][y] == TileObject.PLAYER.ordinal()
        || tiles[x][y] == TileObject.ITEM.ordinal();
  }
}
