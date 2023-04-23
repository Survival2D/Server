package survival2d.match.entity.quadtree;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.val;
import survival2d.match.util.MatchUtil;

@Getter
public class QuadTree<T extends Node> {
  private static final int CAPACITY = 4; // Sức chứa của mỗi quadtree
  Rectangle treeBoundary;
  boolean partitioned = false;
  Map<Integer, T> nodes = new ConcurrentHashMap<>();
  QuadTree<T> northwest;
  QuadTree<T> northeast;
  QuadTree<T> southwest;
  QuadTree<T> southeast;

  public QuadTree(float x, float y, float width, float height) {
    treeBoundary = new Rectangle(x, y, width, height);
  }

  public QuadTree(Rectangle boundary) {
    treeBoundary = boundary;
  }

  public void add(T node) {
    if (!treeBoundary.contains(node.getPosition())) return;

    if (nodes.containsKey(node.getId())) return;

    if (nodes.size() < CAPACITY) {
      nodes.put(node.getId(), node);
      return;
    }

    if (!partitioned) partition();
    if (northwest.treeBoundary.contains(node.getPosition())) {
      northwest.add(node);
    } else if (northeast.treeBoundary.contains(node.getPosition())) {
      northeast.add(node);
    } else if (southwest.treeBoundary.contains(node.getPosition())) {
      southwest.add(node);
    } else if (southeast.treeBoundary.contains(node.getPosition())) {
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
    val x = treeBoundary.x;
    val y = treeBoundary.y;
    val width = treeBoundary.width;
    val height = treeBoundary.height;
    val halfWidth = width / 2;
    val halfHeight = height / 2;
    northwest = new QuadTree<>(x, y, halfWidth, halfHeight);
    northeast = new QuadTree<>(x, y + halfHeight, halfWidth, halfHeight);
    southwest = new QuadTree<>(x + halfWidth, y, halfWidth, halfHeight);
    southeast = new QuadTree<>(x + halfWidth, y + halfHeight, halfWidth, halfHeight);
    partitioned = true;
  }

  public List<T> query(Shape2D boundary) {
    if (MatchUtil.isIntersect(treeBoundary,boundary)) {
      val result = nodes.values().stream().filter(node -> boundary.contains(node.getPosition())).collect(Collectors.toList());
      if (partitioned) {
        result.addAll(northwest.query(boundary));
        result.addAll(northeast.query(boundary));
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
