package com.survival2d.server.match;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Vector {

  public double x;
  public double y;

  public static Vector add(Vector v1, Vector v2) {
    return new Vector(v1.x + v2.x, v1.y + v2.y);
  }

  public void add(Vector v) {
    this.x += v.x;
    this.y += v.y;
  }
}
