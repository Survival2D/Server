package survival2d.network.json.request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import survival2d.network.json.BaseJsonPacket;
import survival2d.network.json.JsonPacketId;
import survival2d.network.json.JsonPacketIdDeserializer;

public abstract class BaseJsonRequest extends BaseJsonPacket {

  private static final Gson gson =
      new GsonBuilder()
          .registerTypeAdapter(JsonPacketId.class, JsonPacketIdDeserializer.getInstance())
          .create();

  public static BaseJsonRequest fromJson(String data) {
    return gson.fromJson(data, BaseJsonRequest.class);
  }
}
