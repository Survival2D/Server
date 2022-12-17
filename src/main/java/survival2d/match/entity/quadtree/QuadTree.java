package survival2d.match.entity.quadtree;

import java.util.Collection;
import java.util.Hashtable;
import lombok.Getter;
import lombok.val;

@Getter
public class QuadTree {
  private static final int CAPACITY = 10; // Sức chứa của mỗi quadtree
  RectangleBoundary boundary;
  boolean partitioned = false;
  Hashtable<Integer, Node> nodes = new Hashtable<>();
  QuadTree northwest;
  QuadTree northeast;
  QuadTree southwest;
  QuadTree southeast;

  public QuadTree(double x, double y, double width, double height) {
    boundary = new RectangleBoundary(x, y, width, height);
  }

  public void add(Node node) {
    if (!boundary.contains(node)) return;

    if (nodes.contains(node)) return;

    if (nodes.size() < CAPACITY) {
      nodes.put(node.getId(), node);
      return;
    }

    if (!partitioned) partition();
    if (northwest.boundary.contains(node)) {
      northwest.add(node);
    } else if (northeast.boundary.contains(node)) {
      northeast.add(node);
    } else if (southwest.boundary.contains(node)) {
      southwest.add(node);
    } else if (southeast.boundary.contains(node)) {
      southeast.add(node);
    }
  }

  public void remove(Node node) {
    nodes.remove(node.getId());
    if (!partitioned) return;
    northwest.remove(node);
    northeast.remove(node);
    southwest.remove(node);
    southeast.remove(node);
  }

  private void partition() {
    val x = boundary.getPosition().getX();
    val y = boundary.getPosition().getY();
    val width = boundary.getShape().getWidth();
    val height = boundary.getShape().getHeight();
    val halfWidth = width / 2;
    val halfHeight = height / 2;
    northwest = new QuadTree(x, y, halfWidth, halfHeight);
    northeast = new QuadTree(x, y + halfHeight, halfWidth, halfHeight);
    southwest = new QuadTree(x + halfWidth, y, halfWidth, halfHeight);
    southeast = new QuadTree(x + halfWidth, y + halfHeight, halfWidth, halfHeight);
    partitioned = true;
  }

  public Collection<Node> query(BaseBoundary boundary, Collection<Node> relevantNodes) {
    if (boundary.isIntersect(boundary)) {
      nodes.values().stream().filter(boundary::contains).forEach(relevantNodes::add);
      if (partitioned) {
        northwest.query(boundary, relevantNodes);
        northeast.query(boundary, relevantNodes);
        southwest.query(boundary, relevantNodes);
        southeast.query(boundary, relevantNodes);
      }
    }
    return relevantNodes;
  }

  public Collection<Node> getAllObjects(Collection<Node> relevantNodes) {
    relevantNodes.addAll(nodes.values());
    if (partitioned) {
      northwest.getAllObjects(relevantNodes);
      northeast.getAllObjects(relevantNodes);
      southwest.getAllObjects(relevantNodes);
      southeast.getAllObjects(relevantNodes);
    }
    return relevantNodes;
  }

  public void update(Node node) {
    remove(node);
    add(node);
  }
}
