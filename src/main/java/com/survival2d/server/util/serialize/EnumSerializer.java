package com.survival2d.server.util.serialize;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EnumSerializer implements JsonSerializer<Enum<?>> {

  @Getter(lazy = true)
  private static final EnumSerializer instance = new EnumSerializer();

  @Override
  public JsonElement serialize(Enum<?> anEnum, Type type,
      JsonSerializationContext jsonSerializationContext) {
    return new JsonPrimitive(anEnum.ordinal());
  }
}
