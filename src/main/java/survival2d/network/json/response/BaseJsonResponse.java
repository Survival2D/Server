package survival2d.network.json.response;

import com.google.gson.Gson;
import survival2d.network.json.BaseJsonPacket;

public abstract class BaseJsonResponse extends BaseJsonPacket {

  private static final Gson gson = new Gson();

  public String toJson() {
    return gson.toJson(this);
  }
}
