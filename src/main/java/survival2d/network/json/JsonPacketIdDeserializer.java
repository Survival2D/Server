package survival2d.network.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import lombok.Getter;

public class JsonPacketIdDeserializer implements JsonDeserializer<JsonPacketId> {

  @Getter private static final JsonPacketIdDeserializer instance = new JsonPacketIdDeserializer();

  @Override
  public JsonPacketId deserialize(
      JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
      throws JsonParseException {
    return JsonPacketId.valueOf(jsonElement.getAsString());
  }
}
