package survival2d.match.util;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.val;
import lombok.var;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class MapGenerator {
  public static final int MAP_WIDTH = 100;
  public static final int MAP_HEIGHT = 100;
  public static final int MAX_DEPT = 4;
  public static final int MIN_RECT_WIDTH = 10;
  public static final int MIN_RECT_HEIGHT = 10;
  public static final int MIN_DOOR = 3;
  public static final int MAX_DOOR = 7;
  public static final int DONT_GEN_BORDER = 2;

  public static final int TYPE_NONE = -1;
  public static final int TYPE_EMPTY = 0;
  public static final int TYPE_WALL = 1;
  public static final int TYPE_TREE = 2;
  public static final int TYPE_BOX = 3;
  public static final int TYPE_ROCK = 4;

  private List<Rectangle> rects;
  private List<Tile> mapObjects;
  private List<Position> mapWalls;
  private int[][] map;

  private MapGenerator() {}

  public static MapGeneratorResult generateMap() {
    val generator = new MapGenerator();
    return generator.generate();
  }

  private boolean isValidPosition(int posX, int posY, Obstacle obstacle) {
    for (var i = 0; i < obstacle.width; i++) {
      for (var j = 0; j < obstacle.height; j++) {
        val newPosX = posX + i;
        val newPosY = posY + j;
        if (newPosX < 0 || newPosY < 0 || newPosX >= MAP_WIDTH || newPosY >= MAP_HEIGHT)
          return false;
        if (map[newPosX][newPosY] != TYPE_EMPTY) return false;
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
    removeIsolatedWall();
    fillMap(mapWalls, Obstacle.WALL);

    for (val pos : mapWalls) {
      mapObjects.add(new Tile(Obstacle.WALL, pos));
    }

    generateOtherObjects();
    return MapGeneratorResult.builder().mapObjects(mapObjects).build();
  }

  private void generateWalls(int offsetX, int offsetY, int width, int height, int depth) {
    if (width <= 2 * MIN_RECT_WIDTH || height <= 2 * MIN_RECT_HEIGHT || depth <= 0) {
      rects.add(new Rectangle(new Position(offsetX, offsetY), width, height));
      return;
    }
    val verticalSplitPosition = RandomUtils.nextInt(MIN_RECT_WIDTH, width - MIN_RECT_WIDTH - 1);
    val horizontalSplitPosition =
        RandomUtils.nextInt(MIN_RECT_HEIGHT, height - MIN_RECT_HEIGHT - 1);

    val topWalls = new LinkedList<Position>();
    val rightWalls = new LinkedList<Position>();
    val botWalls = new LinkedList<Position>();
    val leftWalls = new LinkedList<Position>();

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

    val longWalls = Lists.newArrayList(topWalls, rightWalls, botWalls, leftWalls);
    longWalls.add(topWalls);
    longWalls.add(rightWalls);
    longWalls.add(botWalls);
    longWalls.add(leftWalls);

    // Chọn 3 trong 4 bức tường để "đục" tường tạo lối đi
    val keepWalls = RandomUtils.nextInt(0, longWalls.size());
    for (var i = 0; i < longWalls.size(); i++) {
      if (i != keepWalls) {
        val removed = removeConsecutiveSegmentRandomly(longWalls.get(i));
        val removedFrom = removed.getLeft();
        val removedTo = removed.getRight();
        val removedWalls = Lists.newArrayList(longWalls.get(i).subList(removedFrom, removedTo + 1));
        longWalls.get(i).removeAll(removedWalls);
      }
    }

    // Thêm các hàng tường đã gen vào list
    mapWalls.add(new Position(offsetX + horizontalSplitPosition, offsetY + verticalSplitPosition));
    mapWalls.addAll(
        longWalls.stream()
            .flatMap(Collection::stream)
            .collect(Collectors.toCollection(LinkedList::new)));

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
    val acceptedWalls = new LinkedList<Position>();
    val isolatedMap = new int[MAP_WIDTH][MAP_HEIGHT];
    for (int i = 0; i < MAP_WIDTH; i++) {
      for (int j = 0; j < MAP_HEIGHT; j++) {
        isolatedMap[i][j] = TYPE_NONE;
      }
    }
    for (val wall : mapWalls) {
      isolatedMap[wall.x][wall.y] = TYPE_WALL;
    }
    for (val wall : mapWalls) {
      var flag = false;
      for (val neighbour : Position._4_NEIGHBOURS) {
        val newX = wall.x + neighbour.x;
        val newY = wall.y + neighbour.y;
        if (Math.min(newX, newY) >= 0
            && newX < MAP_WIDTH
            && newY < MAP_HEIGHT
            && isolatedMap[newX][newY] != TYPE_WALL) {
          flag = true;
          break;
        }
      }
      if (flag) {
        acceptedWalls.add(wall);
      }
    }
    mapWalls = acceptedWalls;
  }

  private Pair<Integer, Integer> removeConsecutiveSegmentRandomly(List<Position> array) {
    if (array.size() <= MIN_DOOR) return new ImmutablePair<>(0, array.size());
    val startPosition = RandomUtils.nextInt(1, Math.max(1, array.size() - MAX_DOOR));
    val endPosition =
        RandomUtils.nextInt(
            Math.min(startPosition + MIN_DOOR - 1, array.size() - 1),
            Math.min(startPosition + MAX_DOOR - 1, array.size() - 1));
    return new ImmutablePair<>(startPosition, endPosition);
  }

  private void generateOtherObjects() {
    val coverageRate = 0.1;

    for (var rect : rects) {

      val offset = rect.position;
      val rectWidth = rect.width;
      val rectHeight = rect.height;
      val offsetX = offset.x;
      val offsetY = offset.y;
      var size = 0;
      val capacity = rectHeight * rectWidth * coverageRate;

      // Duyệt tất cả tile trong rect theo thứ tự random
      val cells = new LinkedList<Position>();
      for (var i = DONT_GEN_BORDER; i < rectHeight - DONT_GEN_BORDER; i++) {

        for (var j = DONT_GEN_BORDER; j < rectWidth - DONT_GEN_BORDER; j++) {
          cells.add(new Position(offsetX + i, offsetY + j));
        }
      }
      Collections.shuffle(cells);

      for (val position : cells) {

        // choose random object, if cannot put object at (posX, posY), remove random object from
        // list then repeat
        Obstacle spawnObject = null;

        val listObjects = Obstacle.getListObstacles();
        //                if str(TYPE_EMPTY) in listObjects:
        //      listObjects.remove(str(TYPE_EMPTY))
        //      if str(TYPE_WALL) in listObjects:
        //      listObjects.remove(str(TYPE_WALL))

        while (!listObjects.isEmpty()) {
          val weights = new LinkedList<Integer>();
          weights.add(0);
          for (val object : listObjects) weights.add(weights.getLast() + object.weight);

          Obstacle randomObject = null;
          val randWeight = ThreadLocalRandom.current().nextInt(weights.getLast());
          for (var i = 1; i < weights.size(); i++) {
            if (weights.get(i - 1) <= randWeight && randWeight < weights.get(i)) {

              randomObject = listObjects.get(i - 1);
              break;
            }
          }

          val objectHeight = randomObject.height;
          val objectWidth = randomObject.width;
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

  private void fillMap(int posX, int posY, Obstacle obstacle) {
    for (var i = 0; i < obstacle.width; i++) {
      for (var j = 0; j < obstacle.height; j++) {
        val newPosX = posX + i;
        val newPosY = posY + j;
        map[newPosX][newPosY] = obstacle.ordinal();
      }
    }
  }

  private void fillMap(List<Position> positions, Obstacle obstacle) {
    for (val position : positions) {
      fillMap(position.x, position.y, obstacle);
    }
  }

  @AllArgsConstructor
  public enum Obstacle {
    WALL(1, 1, 1),
    TREE(1, 1, 1),
    BOX(2, 2, 1),
    ROCK(2, 2, 1);

    //    int type;
    int width;
    int height;
    int weight;

    public static List<Obstacle> getListObstacles() {
      return new LinkedList<>(Arrays.asList(Obstacle.values()));
    }

    public static TreeMap<Integer, Obstacle> buildObstacleWeight() {
      val map = new TreeMap<Integer, Obstacle>();
      var weight = 0;
      for (val obstacle : Obstacle.values()) {
        weight += obstacle.weight;
        map.put(weight, obstacle);
      }
      return map;
    }
  }

  @AllArgsConstructor
  @Getter
  @ToString
  public static class Tile {
    Obstacle type;
    Position position;
  }

  @AllArgsConstructor
  @Getter
  @ToString
  public static class Position {
    public static List<Position> _4_NEIGHBOURS =
        Arrays.asList(
            new Position(-1, 0), new Position(1, 0), new Position(0, -1), new Position(0, 1));
    int x;
    int y;

    public Position(Position position) {
      this.x = position.x;
      this.y = position.y;
    }
  }

  @AllArgsConstructor
  public static class Rectangle {
    Position position;
    int width;
    int height;
  }

  @Builder
  @Getter
  @ToString
  public static class MapGeneratorResult {
    List<Tile> mapObjects;
  }
}
