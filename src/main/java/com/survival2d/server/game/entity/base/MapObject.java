package com.survival2d.server.game.entity.base;

import com.survival2d.server.game.entity.Property;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public interface MapObject {

  void set(Property key, Object value);

  Object get(Property key);

  <T> Stream<T> children(Property key, Function<Map<String, Object>, T> constructor);

  String toJson();
}
