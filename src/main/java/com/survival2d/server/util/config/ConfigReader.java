package com.survival2d.server.util.config;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.survival2d.server.util.resource.ResourcesUtil;
import com.survival2d.server.util.serialize.GsonHolder;
import java.io.InputStreamReader;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
public class ConfigReader {

  public static <T> T fromFile(String fileName, Class<T> classOfT) {
    return fromFile(fileName, classOfT, GsonHolder.getNormalGson());
  }

  public static <T> T fromFile(String fileName, Class<T> classOfT, Gson gson) {
    try {
      val stream = ResourcesUtil.getInstance().getFileFromResourceAsStream(fileName);
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
