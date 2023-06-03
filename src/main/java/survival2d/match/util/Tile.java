package survival2d.match.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class Tile {

  TileObject type;
  Position position;

  public static class TileSerializer implements JsonSerializer<Tile> {
    public static final TileSerializer INSTANCE = new TileSerializer();

    @Override
    public JsonElement serialize(Tile src, Type typeOfSrc, JsonSerializationContext context) {
      if (src.type == TileObject.EMPTY
          || src.type == TileObject.PLAYER
          || src.type == TileObject.ITEM) return null;
      var jsonArray = new JsonArray();
      jsonArray.add(src.type.ordinal());
      var positionData = new JsonArray();
      positionData.add(src.position.x);
      positionData.add(src.position.y);
      jsonArray.add(positionData);
      return jsonArray;
    }
  }
}
