package survival2d.match.util;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class MapGenerator {
  public static final int MAP_WIDTH = 50;
  public static final int MAP_HEIGHT = 50;
  public static final int MAX_DEPT = 4;
  public static final int MIN_RECT_WIDTH = 10;
  public static final int MIN_RECT_HEIGHT = 10;
  public static final int MIN_DOOR = 3;
  public static final int MAX_DOOR = 7;
  public static final int DONT_GEN_BORDER = 2;
  public static final int TILE_SIZE = 100;

  private List<Rectangle> rects;
  private List<Tile> mapObjects;
  private List<Position> mapWalls;
  private int[][] map;

  private MapGenerator() {}

  public static MapGeneratorResult generateMap() {
    var generator = new MapGenerator();
    return generator.generate();
  }

  private boolean isValidPosition(int posX, int posY, TileObject tileObject) {
    for (var i = 0; i < tileObject.width; i++) {
      for (var j = 0; j < tileObject.height; j++) {
        var newPosX = posX + i;
        var newPosY = posY + j;
        if (newPosX < 0 || newPosY < 0 || newPosX >= MAP_WIDTH || newPosY >= MAP_HEIGHT)
          return false;
        if (map[newPosX][newPosY] != TileObject.EMPTY.ordinal()) return false;
      }
    }
    return true;
  }

  public MapGeneratorResult generate() {
    rects = Lists.newArrayList();
    mapObjects = Lists.newArrayList();
    mapWalls = Lists.newArrayList();
    map = new int[MAP_WIDTH][MAP_HEIGHT];
    generateWalls(0, 0, MAP_WIDTH, MAP_HEIGHT, MAX_DEPT);
    fillMap(mapWalls, TileObject.WALL);
    removeIsolatedWall();

    for (var pos : mapWalls) {
      mapObjects.add(new Tile(TileObject.WALL, pos));
    }

    generateOtherObjects();
    return new MapGeneratorResult(map, mapObjects);
  }

  private void generateWalls(int offsetX, int offsetY, int width, int height, int depth) {
    if (width <= 2 * MIN_RECT_WIDTH || height <= 2 * MIN_RECT_HEIGHT || depth <= 0) {
      rects.add(new Rectangle(offsetX, offsetY, width, height));
      return;
    }
    var verticalSplitPosition = RandomUtils.nextInt(MIN_RECT_WIDTH, width - MIN_RECT_WIDTH - 1);
    var horizontalSplitPosition =
        RandomUtils.nextInt(MIN_RECT_HEIGHT, height - MIN_RECT_HEIGHT - 1);

    var topWalls = new LinkedList<Position>();
    var rightWalls = new LinkedList<Position>();
    var botWalls = new LinkedList<Position>();
    var leftWalls = new LinkedList<Position>();

    for (var i = 0; i < height; i++) {
      if (i < horizontalSplitPosition)
        topWalls.add(new Position(offsetX + i, offsetY + verticalSplitPosition));
      else if (i > horizontalSplitPosition)
        botWalls.add(new Position(offsetX + i, offsetY + verticalSplitPosition));
    }

    for (var i = 0; i < width; i++) {
      if (i < verticalSplitPosition)
        leftWalls.add(new Position(offsetX + horizontalSplitPosition, offsetY + i));
      else if (i > verticalSplitPosition)
        rightWalls.add(new Position(offsetX + horizontalSplitPosition, offsetY + i));
    }

    var longWalls = Lists.newArrayList(topWalls, rightWalls, botWalls, leftWalls);
    longWalls.add(topWalls);
    longWalls.add(rightWalls);
    longWalls.add(botWalls);
    longWalls.add(leftWalls);

    var keepWalls =
        RandomUtils.nextFloat() < 1f / 4 ? RandomUtils.nextInt(0, longWalls.size()) : -1;
    for (var i = 0; i < longWalls.size(); i++) {
      var walls = longWalls.get(i);
      if (i != keepWalls || walls.size() >= 10) {
        var removed = removeConsecutiveSegmentRandomly(walls);
        var removedFrom = removed.getLeft();
        var removedTo = removed.getRight();
        var removedWalls = Lists.newArrayList(walls.subList(removedFrom, removedTo + 1));
        walls.removeAll(removedWalls);
      }
    }

    // Thêm các hàng tường đã gen vào list
    mapWalls.add(new Position(offsetX + horizontalSplitPosition, offsetY + verticalSplitPosition));
    mapWalls.addAll(longWalls.stream().flatMap(Collection::stream).toList());

    // Gen cho 4 ô nhỏ và thêm vào kết quả

    generateWalls(offsetX, offsetY, verticalSplitPosition, horizontalSplitPosition, depth - 1);

    generateWalls(
        offsetX,
        offsetY + verticalSplitPosition + 1,
        width - verticalSplitPosition - 1,
        horizontalSplitPosition,
        depth - 1);

    generateWalls(
        offsetX + horizontalSplitPosition + 1,
        offsetY + verticalSplitPosition + 1,
        width - verticalSplitPosition - 1,
        height - horizontalSplitPosition - 1,
        depth - 1);

    generateWalls(
        offsetX + horizontalSplitPosition + 1,
        offsetY,
        verticalSplitPosition,
        height - horizontalSplitPosition - 1,
        depth - 1);
  }

  private void removeIsolatedWall() {
    var acceptedWalls = new LinkedList<Position>();
    for (var wall : mapWalls) {
      var flag = false;
      for (var neighbour : Position.NEIGHBOURS) {
        var newX = wall.x + neighbour.x;
        var newY = wall.y + neighbour.y;
        if (newX >= 0
            && newY >= 0
            && newX < MAP_WIDTH
            && newY < MAP_HEIGHT
            && map[newX][newY] == TileObject.WALL.ordinal()) {
          flag = true;
          break;
        }
      }
      if (flag) {
        acceptedWalls.add(wall);
      } else {
        map[wall.x][wall.y] = TileObject.EMPTY.ordinal();
      }
    }
    mapWalls = acceptedWalls;
  }

  private Pair<Integer, Integer> removeConsecutiveSegmentRandomly(List<Position> array) {
    if (array.size() <= MIN_DOOR) return new ImmutablePair<>(0, array.size());
    var startPosition = RandomUtils.nextInt(1, Math.max(1, array.size() - MAX_DOOR));
    var endPosition =
        RandomUtils.nextInt(
            Math.min(startPosition + MIN_DOOR - 1, array.size() - 1),
            Math.min(startPosition + MAX_DOOR - 1, array.size() - 1));
    return new ImmutablePair<>(startPosition, endPosition);
  }

  private void generateOtherObjects() {
    var coverageRate = 0.1;

    for (var rect : rects) {
      var rectWidth = rect.width;
      var rectHeight = rect.height;
      var offsetX = rect.x;
      var offsetY = rect.y;
      var size = 0;
      var capacity = rectHeight * rectWidth * coverageRate;

      // Duyệt tất cả tile trong rect theo thứ tự random
      var cells = new LinkedList<Position>();
      for (var i = DONT_GEN_BORDER; i < rectHeight - DONT_GEN_BORDER; i++) {
        for (var j = DONT_GEN_BORDER; j < rectWidth - DONT_GEN_BORDER; j++) {
          cells.add(new Position(offsetX + i, offsetY + j));
        }
      }
      Collections.shuffle(cells);
      var first = cells.removeFirst();
      fillMap(first.x, first.y, TileObject.PLAYER);
      mapObjects.add(new Tile(TileObject.PLAYER, first));

      for (var position : cells) {

        // choose random object, if cannot put object at (posX, posY), remove random object from
        // list then repeat
        TileObject spawnObject = null;

        var listObjects = TileObject.getListObstacles();
        //                if str(TYPE_EMPTY) in listObjects:
        //      listObjects.remove(str(TYPE_EMPTY))
        //      if str(TYPE_WALL) in listObjects:
        //      listObjects.remove(str(TYPE_WALL))

        while (!listObjects.isEmpty()) {
          var weights = new LinkedList<Integer>();
          weights.add(0);
          for (var object : listObjects) weights.add(weights.getLast() + object.weight);

          TileObject randomObject = null;
          var randWeight = ThreadLocalRandom.current().nextInt(weights.getLast());
          for (var i = 1; i < weights.size(); i++) {
            if (weights.get(i - 1) <= randWeight && randWeight < weights.get(i)) {

              randomObject = listObjects.get(i - 1);
              break;
            }
          }

          var objectHeight = randomObject.height;
          var objectWidth = randomObject.width;
          if (isValidPosition(position.x, position.y, randomObject)
              && size + objectHeight * objectWidth <= capacity) {
            spawnObject = randomObject;
            break;
          } else {
            listObjects.remove(randomObject);
          }
        }

        if (spawnObject != null) {
          size += spawnObject.width * spawnObject.height;
          fillMap(position.x, position.y, spawnObject);
          mapObjects.add(new Tile(spawnObject, position));
        }
      }
    }
  }

  private void fillMap(int posX, int posY, TileObject tileObject) {
    for (var i = 0; i < tileObject.width; i++) {
      for (var j = 0; j < tileObject.height; j++) {
        var newPosX = posX + i;
        var newPosY = posY + j;
        map[newPosX][newPosY] = tileObject.ordinal();
      }
    }
  }

  private void fillMap(List<Position> positions, TileObject tileObject) {
    for (var position : positions) {
      fillMap(position.x, position.y, tileObject);
    }
  }
}
