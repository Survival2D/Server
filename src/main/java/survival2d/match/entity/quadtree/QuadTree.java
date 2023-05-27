package survival2d.match.entity.quadtree;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.Getter;
import survival2d.match.util.MatchUtil;

@Getter
public class QuadTree<T extends Node> {
  private static final int CAPACITY = 4; // Sức chứa của mỗi quadtree
  Rectangle treeBoundary;
  boolean partitioned = false;
  Map<Integer, T> nodes = new ConcurrentHashMap<>();
  QuadTree<T> quadrant1;
  QuadTree<T> quadrant2;
  QuadTree<T> quadrant3;
  QuadTree<T> quadrant4;

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
    if (quadrant1.treeBoundary.contains(node.getPosition())) {
      quadrant1.add(node);
    } else if (quadrant2.treeBoundary.contains(node.getPosition())) {
      quadrant2.add(node);
    } else if (quadrant3.treeBoundary.contains(node.getPosition())) {
      quadrant3.add(node);
    } else if (quadrant4.treeBoundary.contains(node.getPosition())) {
      quadrant4.add(node);
    }
  }

  public void remove(T node) {
    nodes.remove(node.getId());
    if (!partitioned) return;
    quadrant3.remove(node);
    quadrant4.remove(node);
    quadrant2.remove(node);
    quadrant1.remove(node);
  }

  private void partition() {
    var x = treeBoundary.x;
    var y = treeBoundary.y;
    var width = treeBoundary.width;
    var height = treeBoundary.height;
    var halfWidth = width / 2;
    var halfHeight = height / 2;
    quadrant1 = new QuadTree<>(x + halfWidth, y + halfHeight, halfWidth, halfHeight);
    quadrant2 = new QuadTree<>(x + halfWidth, y, halfWidth, halfHeight);
    quadrant3 = new QuadTree<>(x, y, halfWidth, halfHeight);
    quadrant4 = new QuadTree<>(x, y + halfHeight, halfWidth, halfHeight);
    partitioned = true;
  }

  public List<T> query(Shape2D boundary) {
    if (MatchUtil.isIntersect(treeBoundary, boundary)) {
      var result =
          nodes.values().stream()
              .filter(node -> MatchUtil.isIntersect(boundary, node.getShape()))
              .collect(Collectors.toList());
      if (partitioned) {
        result.addAll(quadrant1.query(boundary));
        result.addAll(quadrant2.query(boundary));
        result.addAll(quadrant3.query(boundary));
        result.addAll(quadrant4.query(boundary));
      }
      return result;
    }
    return Collections.emptyList();
  }

  public Collection<T> getAllObjects(Collection<T> relevantNodes) {
    relevantNodes.addAll(nodes.values());
    if (partitioned) {
      quadrant1.getAllObjects(relevantNodes);
      quadrant2.getAllObjects(relevantNodes);
      quadrant3.getAllObjects(relevantNodes);
      quadrant4.getAllObjects(relevantNodes);
    }
    return relevantNodes;
  }

  public void update(T node) {
    remove(node);
    add(node);
  }
}
