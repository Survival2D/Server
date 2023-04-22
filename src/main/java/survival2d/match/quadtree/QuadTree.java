package survival2d.match.quadtree;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.val;

@Getter
public class QuadTree<T extends Node> {
  private static final int CAPACITY = 4; // Sức chứa của mỗi quadtree
  RectangleBoundary boundary;
  boolean partitioned = false;
  Map<Integer, T> nodes = new ConcurrentHashMap<>();
  QuadTree<T> northwest;
  QuadTree<T> northeast;
  QuadTree<T> southwest;
  QuadTree<T> southeast;

  public QuadTree(double x, double y, double width, double height) {
    boundary = new RectangleBoundary(x, y, width, height);
  }

  public QuadTree(RectangleBoundary boundary) {
    this.boundary = boundary;
  }

  public void add(T node) {
    if (!boundary.contains(node)) return;

    if (nodes.containsKey(node.getId())) return;

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

  public void remove(T node) {
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
    northwest = new QuadTree<>(x, y, halfWidth, halfHeight);
    northeast = new QuadTree<>(x, y + halfHeight, halfWidth, halfHeight);
    southwest = new QuadTree<>(x + halfWidth, y, halfWidth, halfHeight);
    southeast = new QuadTree<>(x + halfWidth, y + halfHeight, halfWidth, halfHeight);
    partitioned = true;
  }

  public List<T> query(BaseBoundary boundary) {
    if (boundary.isIntersect(boundary)) {
      val result = nodes.values().stream().filter(boundary::contains).collect(Collectors.toList());
      if (partitioned) {
        result.addAll(northwest.query(boundary ));
        result.addAll(northeast.query(boundary ));
        result.addAll(southwest.query(boundary));
        result.addAll(southeast.query(boundary));
      }
      return result;
    }
    return Collections.emptyList();
  }

  public Collection<T> getAllObjects(Collection<T> relevantNodes) {
    relevantNodes.addAll(nodes.values());
    if (partitioned) {
      northwest.getAllObjects(relevantNodes);
      northeast.getAllObjects(relevantNodes);
      southwest.getAllObjects(relevantNodes);
      southeast.getAllObjects(relevantNodes);
    }
    return relevantNodes;
  }

  public void update(T node) {
    remove(node);
    add(node);
  }
}
