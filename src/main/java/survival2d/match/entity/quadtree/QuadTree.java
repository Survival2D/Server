package survival2d.match.entity.quadtree;

import java.util.Collection;
import java.util.Hashtable;
import lombok.Getter;
import lombok.val;

@Getter
public class QuadTree {
  private static final int CAPACITY = 4; // Sức chứa của mỗi quadtree
  RectangleBoundary boundary;
  boolean partitioned = false;
  Hashtable<Integer, Node<?>> nodes = new Hashtable<>();
  QuadTree northwest;
  QuadTree northeast;
  QuadTree southwest;
  QuadTree southeast;

  QuadTree(double x, double y, double width, double height) {
    boundary = new RectangleBoundary(x, y, width, height);
  }

  public void add(BaseMapObject object) {
    if (!boundary.contains(object)) return;

    if (nodes.size() < CAPACITY) {
      nodes.put(object.id, object);
      return;
    }

    if (!partitioned) partition();
    if (northwest.boundary.contains(object)) {
      northwest.add(object);
    } else if (northeast.boundary.contains(object)) {
      northeast.add(object);
    } else if (southwest.boundary.contains(object)) {
      southwest.add(object);
    } else if (southeast.boundary.contains(object)) {
      southeast.add(object);
    }
  }

  private void partition() {
    val x = boundary.getPosition().getX();
    val y = boundary.getPosition().getY();
    val width = boundary.getShape().getWidth();
    val height = boundary.getShape().getHeight();
    val halfWidth = width / 2;
    val halfHeight = height / 2;
    this.northwest = new QuadTree(x, y, halfWidth, halfHeight);
    this.northeast = new QuadTree(x, y + halfHeight, halfWidth, halfHeight);
    this.southwest = new QuadTree(x + halfWidth, y, halfWidth, halfHeight);
    this.southeast = new QuadTree(x + halfWidth, y + halfHeight, halfWidth, halfHeight);
    this.partitioned = true;
  }

  public Collection<Node<?>> query(Boundary boundary, Collection<Node<?>> relevantNodes) {
    if (this.boundary.isIntersect(boundary)) {
      this.nodes.values().stream().filter(boundary::contains).forEach(relevantNodes::add);
      if (this.partitioned) {
        this.northwest.query(boundary, relevantNodes);
        this.northeast.query(boundary, relevantNodes);
        this.southwest.query(boundary, relevantNodes);
        this.southeast.query(boundary, relevantNodes);
      }
    }
    return relevantNodes;
  }
}
