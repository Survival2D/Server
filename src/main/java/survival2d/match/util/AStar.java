package survival2d.match.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/** <a href="https://www.geeksforgeeks.org/a-search-algorithm/">Source</a> */
@Slf4j
public class AStar {
  protected int[][] grid; // Base
  @Setter protected boolean allowDiagonals = false; // Setting - Có thể đi chéo không
  int row; // Base
  int col; // Base
  AStarCell[][] cells; // Base - Lưu cell ở đây để dùng lại, tránh khởi tạo lại

  public AStar(int row, int col, int[][] grid) {
    this.row = row;
    this.col = col;
    this.grid = grid;
    cells = new AStarCell[row][col];
    for (int i = 0; i < row; i++) {
      for (int j = 0; j < col; j++) {
        cells[i][j] = new AStarCell(i, j);
      }
    }
  }

  private boolean isValid(Point p) {
    return p.x >= 0 && p.x < row && p.y >= 0 && p.y < col;
  }

  protected boolean isUnBlocked(Point p) {
    return grid[p.x][p.y] == TileObject.EMPTY.ordinal()
        || grid[p.x][p.y] == TileObject.PLAYER.ordinal()
        || grid[p.x][p.y] == TileObject.ITEM.ordinal();
  }

  private boolean isDestination(Point p, Point dest) {
    //    log.debug("isDestination: {} {}", dest, p);
    return dest.equals(p);
  }

  protected double calculateHValue(Point p, Point dest) {
    return Math.sqrt(
        (p.getX() - dest.getX()) * (p.getX() - dest.getX())
            + (p.getY() - dest.getY()) * (p.getY() - dest.getY()));
  }

  protected double calculateGValue(AStarCell cell) {
    return cell.g + 1;
  }

  protected AStarCell getCellAtPoint(Point p) {
    if (isValid(p)) {
      return cells[p.x][p.y];
    }
    return null;
  }

  private List<AStarCell> get8Neighbours(AStarCell cell) {
    return Point.EIGHT_NEIGHBOURS.stream()
        .map(p -> getCellAtPoint(cell.add(p)))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private List<AStarCell> get4Neighbours(AStarCell cell) {
    return Point.FOUR_NEIGHBOURS.stream()
        .map(p -> getCellAtPoint(cell.add(p)))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private List<Point> getPath(Point dest) {
    var cell = getCellAtPoint(dest);
    LinkedList<Point> path = new LinkedList<>();
    while (!(getCellAtPoint(cell).parent == cell)) {
      path.addFirst(cell);
      cell = cell.parent;
    }
    path.addFirst(cell);
    log.debug("The path is {}", path);
    return path;
  }

  Point suggestDest(Point dest) {
    if (isValid(dest) && isUnBlocked(dest)) {
      return dest;
    }
    for (Point p : Point.FOUR_NEIGHBOURS) {
      Point newDest = dest.add(p);
      if (isValid(newDest) && isUnBlocked(newDest)) {
        return newDest;
      }
    }
    for (int i = 0; i < 100; i++) {
      var randomPoint =
          new Point(
              ThreadLocalRandom.current().nextInt(row), ThreadLocalRandom.current().nextInt(col));
      if (isValid(randomPoint) && isUnBlocked(randomPoint)) {
        return randomPoint;
      }
    }
    return dest;
  }

  public List<Point> aStarSearch(Point src, Point dest) {
    dest = suggestDest(dest);

    if (!isValid(src)) {
      log.warn("Source is invalid");
      return Collections.emptyList();
    }

    if (!isValid(dest)) {
      log.warn("Destination is invalid");
      return Collections.emptyList();
    }

    if (!isUnBlocked(src) || !isUnBlocked(dest)) {
      log.warn("Source or the destination is blocked");
      return Collections.emptyList();
    }

    if (isDestination(src, dest)) {
      log.warn("We are already at the destination");
      return Collections.emptyList();
    }

    // Reset cells data
    for (int i = 0; i < row; i++) {
      for (int j = 0; j < col; j++) {
        cells[i][j].g = Double.MAX_VALUE;
        cells[i][j].h = Double.MAX_VALUE;
        cells[i][j].parent = null;
      }
    }

    var beginCell = getCellAtPoint(src);
    beginCell.g = 0;
    beginCell.h = 0;
    beginCell.setParent(beginCell);

    Set<AStarCell> closed = new HashSet<>();
    Queue<AStarCell> open = new PriorityQueue<>();
    open.add(beginCell);

    while (!open.isEmpty()) {
      var cell = open.remove();
      closed.add(cell);

      var neighbours = getNeighbours(cell);

      for (var neighbour : neighbours) {
        if (isDestination(neighbour, dest)) {
          neighbour.setParent(cell);
          return getPath(dest);
        } else if (!closed.contains(neighbour) && isUnBlocked(neighbour)) {
          double gNew = calculateGValue(cell);
          double hNew = calculateHValue(neighbour, dest);
          double fNew = gNew + hNew;
          if (neighbour.getF() > fNew) {
            neighbour.g = gNew;
            neighbour.h = hNew;
            open.add(neighbour);
            neighbour.setParent(cell);
          }
        }
      }
    }

    log.warn("Failed to find the Destination Cell\n");
    return Collections.emptyList();
  }

  private List<AStarCell> getNeighbours(AStarCell cell) {
    return allowDiagonals ? get8Neighbours(cell) : get4Neighbours(cell);
  }
}
