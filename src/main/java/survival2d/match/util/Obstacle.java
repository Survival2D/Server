package survival2d.match.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import lombok.AllArgsConstructor;
import lombok.val;
import lombok.var;

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
