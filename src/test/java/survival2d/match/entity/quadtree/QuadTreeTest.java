package survival2d.match.entity.quadtree;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedList;
import java.util.List;
import lombok.val;
import lombok.var;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.junit.jupiter.api.Test;
import survival2d.match.config.GameConfig;
import survival2d.match.entity.base.MapObject;
import survival2d.match.entity.obstacle.Container;
import survival2d.match.entity.obstacle.Stone;
import survival2d.match.entity.obstacle.Tree;
import survival2d.match.entity.obstacle.Wall;
import survival2d.match.util.MapGenerator;
import survival2d.match.util.Tile;
import survival2d.match.util.TileObject;

class QuadTreeTest {

  @Test
  public void testQuadTreePerformance() {
    val withoutQuadTreeDurations = new LinkedList<Long>();
    val withQuadTreeDurations = new LinkedList<Long>();
    for (var i = 0; i < 1000; i++) {
      val mapGeneratorResult = MapGenerator.generateMap();
      val objects = mapGeneratorResult.getMapObjects();
      val mapObjects = initMapObjects(objects);
      val beginX = RandomUtils.nextDouble(0, GameConfig.getInstance().getMapWidth());
      val beginY = RandomUtils.nextDouble(0, GameConfig.getInstance().getMapHeight());
      val endX = RandomUtils.nextDouble(beginX, GameConfig.getInstance().getMapWidth());
      val endY = RandomUtils.nextDouble(beginY, GameConfig.getInstance().getMapHeight());
      val boundary = new RectangleBoundary(beginX, beginY, endX - beginX, endY - beginY);
      val withoutQuadTree = findCollisionWithoutQuadTree(mapObjects, boundary);
      val withQuadTree = findCollisionWithQuadTree(mapObjects, boundary);
      assertTrue(
          CollectionUtils.isEqualCollection(withoutQuadTree.getLeft(), withQuadTree.getLeft()));
      withoutQuadTreeDurations.add(withoutQuadTree.getRight());
      withQuadTreeDurations.add(withQuadTree.getRight());
    }
    val minWithoutQuadTreeDuration = withoutQuadTreeDurations.stream().min(Long::compareTo).get();
    val maxWithoutQuadTreeDuration = withoutQuadTreeDurations.stream().max(Long::compareTo).get();
    val avgWithoutQuadTreeDuration =
        withoutQuadTreeDurations.stream().mapToLong(Long::longValue).average().getAsDouble();

    val minWithQuadTreeDuration = withQuadTreeDurations.stream().min(Long::compareTo).get();
    val maxWithQuadTreeDuration = withQuadTreeDurations.stream().max(Long::compareTo).get();
    val avgWithQuadTreeDuration =
        withQuadTreeDurations.stream().mapToLong(Long::longValue).average().getAsDouble();

    System.out.println("Without QuadTree");
    System.out.println("Min: " + minWithoutQuadTreeDuration);
    System.out.println("Max: " + maxWithoutQuadTreeDuration);
    System.out.println("Avg: " + avgWithoutQuadTreeDuration);

    System.out.println("With QuadTree");
    System.out.println("Min: " + minWithQuadTreeDuration);
    System.out.println("Max: " + maxWithQuadTreeDuration);
    System.out.println("Avg: " + avgWithQuadTreeDuration);
  }

  private Pair<List<MapObject>, Long> findCollisionWithoutQuadTree(
      List<MapObject> mapObjects, RectangleBoundary boundary) {
    val begin = System.currentTimeMillis();
    val result = new LinkedList<MapObject>();
    for (val mapObject : mapObjects) {
      if (boundary.contains(mapObject)) {
        result.add(mapObject);
      }
    }
    val end = System.currentTimeMillis();
//    System.out.println("Without quad tree: " + (end - begin));
    return new ImmutablePair<>(result, end - begin);
  }

  private Pair<List<MapObject>, Long> findCollisionWithQuadTree(
      List<MapObject> mapObjects, RectangleBoundary boundary) {
    val quadTree =
        new QuadTree<MapObject>(
            0, 0, GameConfig.getInstance().getMapWidth(), GameConfig.getInstance().getMapHeight());
    for (val mapObject : mapObjects) {
      quadTree.add(mapObject);
    }
    val begin = System.currentTimeMillis();
    val result = quadTree.query(boundary);
    val end = System.currentTimeMillis();
//    System.out.println("With quad tree: " + (end - begin));
    return new ImmutablePair<>(result, end - begin);
  }

  private List<MapObject> initMapObjects(List<Tile> objects) {
    val result = new LinkedList<MapObject>();
    for (val tileObject : objects) {
      val position =
          new Vector2D(
              tileObject.getPosition().getX() * MapGenerator.TILE_SIZE,
              tileObject.getPosition().getY() * MapGenerator.TILE_SIZE);
      MapObject mapObject = null;
      switch (tileObject.getType()) {
        case PLAYER:
        case ITEM:
          {
            break;
          }
        case WALL:
          {
            mapObject = new Wall();
            mapObject.setPosition(position);
            break;
          }
        case TREE:
          {
            mapObject = new Tree();
            mapObject.setPosition(position.add(TileObject.TREE.getCenterOffset()));
            break;
          }
        case BOX:
          {
            mapObject = new Container();
            mapObject.setPosition(position);
            break;
          }
        case ROCK:
          {
            mapObject = new Stone();
            mapObject.setPosition(position.add(TileObject.ROCK.getCenterOffset()));
            break;
          }
      }
      if (mapObject != null) {
        mapObject.setId(result.size());
        result.add(mapObject);
      }
    }
    return result;
  }
}
