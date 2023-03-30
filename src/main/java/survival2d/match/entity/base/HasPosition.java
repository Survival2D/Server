package survival2d.match.entity.base;


import org.apache.commons.math3.geometry.euclidean.twod.Vector2;

public interface HasPosition {

  Vector2 getPosition();

  void setPosition(Vector2 position);
}
