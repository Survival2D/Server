package survival2d.match.util;

import lombok.val;
import org.junit.jupiter.api.Test;

class MapGeneratorTest {
  @Test
  public void testGenerateMap() {
    val result = MapGenerator.generateMap();
//    System.out.println(result.mapObjects);
    result.printWalls();
    result.printTiles();
  }
}
