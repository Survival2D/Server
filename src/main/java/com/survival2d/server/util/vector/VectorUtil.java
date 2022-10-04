package com.survival2d.server.util.vector;


import org.locationtech.jts.math.Vector2D;

public class VectorUtil {

  public static final Vector2D ZERO = new Vector2D(0, 0);

  public static boolean isZero(Vector2D vector) {
    return vector.equals(ZERO);
  }

}
