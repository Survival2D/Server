package survival2d.match.entity.base;


import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public interface Movable extends HasPosition {

  default void moveBy(Vector2D v) {
    setPosition(getPosition().add(v));
  }

  default void moveTo(Vector2D v) {
    setPosition(v);
  }
}
