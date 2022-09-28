package com.survival2d.server.util;

import com.google.gson.Gson;

public class GameUtil {

  private static final Gson gson = new Gson();

  public static String toGson(Object obj) {
    return gson.toJson(obj);
  }
}
