package survival2d.match.util;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;

class MapGeneratorTest {
  @Test
  public void testGenerateMap() {
    var result = MapGenerator.generateMap();
    result.printWalls();
    result.printTiles();
    var gson =
        new GsonBuilder().registerTypeAdapter(Tile.class, Tile.TileSerializer.INSTANCE).create();
    System.out.println(gson.toJson(result.mapObjects));
  }
}
