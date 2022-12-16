package survival2d.match.entity.quadtree;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

@AllArgsConstructor
@Getter
@Setter
public abstract class Node<T extends Node<?>> {
  int id;
  Vector2D position;

  public abstract boolean isCollision(T other);
}
