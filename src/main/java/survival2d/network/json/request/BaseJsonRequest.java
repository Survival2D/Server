package survival2d.network.json.request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.miti99.netty.json.BaseJsonPacket;
import com.miti99.netty.json.JsonPacketId;
import com.miti99.netty.json.JsonPacketIdDeserializer;

public abstract class BaseJsonRequest extends BaseJsonPacket {

  private static final Gson gson =
      new GsonBuilder()
          .registerTypeAdapter(JsonPacketId.class, JsonPacketIdDeserializer.getInstance())
          .create();

  public static BaseJsonRequest fromJson(String data) {
    return gson.fromJson(data, BaseJsonRequest.class);
  }
}
