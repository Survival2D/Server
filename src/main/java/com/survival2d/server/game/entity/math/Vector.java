package com.survival2d.server.game.entity.math;

import com.survival2d.server.util.serialize.GsonHolder;
import com.tvd12.ezyfox.binding.EzyMarshaller;
import com.tvd12.ezyfox.binding.EzyWriter;
import com.tvd12.ezyfox.binding.annotation.EzyWriterImpl;
import com.tvd12.ezyfox.entity.EzyHashMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.val;

@Data
@AllArgsConstructor
public class Vector {

  public double x;
  public double y;

  public static Vector add(Vector v1, Vector v2) {
    return new Vector(v1.x + v2.x, v1.y + v2.y);
  }

  public static double length(Vector v) {
    return Math.sqrt(v.x * v.x + v.y * v.y);
  }

  public static Vector unit(Vector v) {
    val length = length(v);
    return new Vector(v.x / length, v.y / length);
  }

  public static boolean isZero(Vector v) {
    return v.x == 0 && v.y == 0;
  }

  public static Vector multiply(Vector v, double n) {
    return new Vector(v.x * n, v.y * n);
  }

  public void add(Vector v) {
    this.x += v.x;
    this.y += v.y;
  }

  @EzyWriterImpl
  public static class VectorWriter implements EzyWriter<Vector, EzyHashMap> {

    @Override
    public EzyHashMap write(EzyMarshaller ezyMarshaller, Vector vector) {
      val data = "{map: " + GsonHolder.getNormalGson().toJson(vector) + "}";
      val map = GsonHolder.getNormalGson().fromJson(data, EzyHashMap.class);
      return map;
    }
  }
}
