package com.survival2d.server.util.vector;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class VectorUtil {
  public static Vector2D multiply(Vector2D vector, double scalar) {
    return new Vector2D(vector.getX() * scalar, vector.getY() * scalar);
  }

}
