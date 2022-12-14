package survival2d.match.util;

import com.tvd12.ezyfox.collect.Lists;
import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.val;
import lombok.var;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import survival2d.match.entity.obstacle.Obstacle;

public class MapGenerator {
  public static final int MAP_WIDTH = 100;
  public static final int MAP_HEIGHT = 100;
  public static final int MAX_DEPT = 4;
  public static final int MIN_RECT_WIDTH = 10;
  public static final int MIN_RECT_HEIGHT = 10;
  public static final int MIN_DOOR = 3;
  public static final int MAX_DOOR = 7;
  public static final int DONT_GEN_BORDER = 2;

  public static List<Obstacle> generateMap() {
    return null;
  }

  private static List<Tile> generateWalls(
      List<Rectangle> rects, Position offset, int width, int height, int depth) {
    if (width <= 2 * MIN_RECT_WIDTH || height <= 2 * MIN_RECT_HEIGHT || depth <= 0) {
      rects.add(new Rectangle(new Position(offset), width, height));
      return null;
    }
    val offsetX = offset.x;
    val offsetY = offset.y;
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

    for (var i = 0; i < width; i++)
      if (i < verticalSplitPosition)
        leftWalls.add(new Position(offsetX + horizontalSplitPosition, offsetY + i));
      else if (i > verticalSplitPosition)
        rightWalls.add(new Position(offsetX + horizontalSplitPosition, offsetY + i));

    val longWalls = Lists.newArrayList(topWalls, rightWalls, botWalls, leftWalls);

    longWalls.add(topWalls);
    longWalls.add(rightWalls);
    longWalls.add(botWalls);
    longWalls.add(leftWalls);
    val keepWalls = RandomUtils.nextInt(0, longWalls.size());
    // Chọn 3 trong 4 bức tường để "đục" tường tạo lối đi

    for (var i = 0; i < longWalls.size(); i++) {

      if (i != keepWalls) {

        val removed = removeConsecutiveSegmentRandomly(longWalls.get(i));
        val removedFrom = removed.getLeft();
        val removedTo = removed.getRight();
        //    longWalls[i] = longWalls[i][:removedFrom] + longWalls[i][removedTo + 1:]
      }
    }

    //        # saved walls position
    //    mapWalls.append((offsetX + horizontalSplitPosition, offsetY + verticalSplitPosition))
    //    for i in range(len(longWalls)):
    //    for j in range(len(longWalls[i])):
    //    mapWalls.append(longWalls[i][j])
    //
    //        # recursive generation for top-left, top-right, bot-right, bot-left
    //    generateWall((offsetX, offsetY), horizontalSplitPosition, verticalSplitPosition, depth -
    // 1)
    //    generateWall((offsetX, offsetY + verticalSplitPosition + 1), horizontalSplitPosition,
    // width - verticalSplitPosition - 1, depth - 1)
    //    generateWall((offsetX + horizontalSplitPosition + 1, offsetY + verticalSplitPosition + 1),
    // height - horizontalSplitPosition - 1, width - verticalSplitPosition - 1, depth - 1)
    //    generateWall((offsetX + horizontalSplitPosition + 1, offsetY), height -
    // horizontalSplitPosition - 1, verticalSplitPosition, depth - 1)

    return null;
  }

  private static Pair<Integer, Integer> removeConsecutiveSegmentRandomly(List<Position> array) {
    if (array.size() <= MIN_DOOR) return new ImmutablePair<>(0, array.size());
    val startPosition = RandomUtils.nextInt(1, Math.max(1, array.size() - MAX_DOOR));
    val endPosition =
        RandomUtils.nextInt(
            Math.min(startPosition + MIN_DOOR - 1, array.size() - 1),
            Math.min(startPosition + MAX_DOOR - 1, array.size() - 1));
    return new ImmutablePair<>(startPosition, endPosition);
  }

  public static class Tile {
    int type;
    Position position;
  }

  @AllArgsConstructor
  public static class Position {
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

  public static class MapGeneratorResult {}
}
