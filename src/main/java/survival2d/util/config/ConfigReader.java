package survival2d.util.config;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.io.InputStreamReader;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import survival2d.util.resource.ResourceUtil;
import survival2d.util.serialize.GsonHolder;

@Slf4j
public class ConfigReader {

  public static <T> T fromFile(String fileName, Class<T> classOfT) {
    return fromFile(fileName, classOfT, GsonHolder.getNormalGson());
  }

  public static <T> T fromFile(String fileName, Class<T> classOfT, Gson gson) {
    try {
      val stream = ResourceUtil.getInstance().getFileFromResourceAsStream(fileName);
      val reader = new InputStreamReader(stream);
      T t = gson.fromJson(new JsonReader(reader), classOfT);
      reader.close();
      stream.close();
      return t;
    } catch (Exception e) {
      log.error("Can not read config from file {} for class {}", fileName, classOfT, e);
      return null;
    }
  }
}
