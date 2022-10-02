package com.survival2d.server.util.serialize;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;

public class GsonHolder {

  @Getter
  private static final Gson normalGson = new Gson();

  @Getter
  private static final Gson responseGson = new GsonBuilder().registerTypeHierarchyAdapter(
      Enum.class, EnumSerializer.getInstance()).create();
}
