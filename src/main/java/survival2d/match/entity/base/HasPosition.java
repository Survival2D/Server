package survival2d.match.entity.base;


import org.locationtech.jts.math.Vector2D;

public interface HasPosition {

  Vector2D getPosition();

  void setPosition(Vector2D position);
}
