package com.survival2d.server.util.serialize;

import com.google.gson.Gson;
import lombok.Getter;

public class GsonHolder {

  @Getter
  private static final Gson normalGson = new Gson();
}
