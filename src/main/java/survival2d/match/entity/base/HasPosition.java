package survival2d.match.entity.base;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public interface HasPosition {

  Vector2D getPosition();

  void setPosition(Vector2D position);
}
