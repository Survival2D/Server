package com.survival2d.server.game.entity.base;

import com.google.gson.Gson;
import com.survival2d.server.game.entity.Property;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.val;

public abstract class AbstractMapObject implements MapObject {

  private static final Gson gson = new Gson();

  private final Map<String, Object> properties;

  protected AbstractMapObject(Map<String, Object> properties) {
    Objects.requireNonNull(properties, "properties map is required");
    this.properties = new ConcurrentHashMap<>(properties);
  }

  @Override
  public final void set(Property key, Object value) {
    properties.put(key.toString(), value);
  }

  @Override
  public final Object get(Property key) {
    return properties.get(key.toString());
  }

  @Override
  public <T> Stream<T> children(Property key, Function<Map<String, Object>, T> constructor) {
    val any = Stream.of(get(key)).filter(Objects::nonNull)
        .map(el -> (List<Map<String, Object>>) el).findAny();
    return any.map(maps -> maps.stream().map(constructor)).orElseGet(Stream::empty);
  }

  @Override
  public final String toJson() {
    return gson.toJson(properties);
  }
}
