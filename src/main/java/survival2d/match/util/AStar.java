package survival2d.match.util;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;

/** <a href="https://www.geeksforgeeks.org/a-search-algorithm/">Source</a> */
@Slf4j
public class AStar {
  protected int[][] grid; // Base
  @Setter protected boolean allowDiagonals = true; // Setting - Có thể đi chéo không
  int row; // Base
  int col; // Base
  Cell[][] cells; // Base - Lưu cell ở đây để dùng lại, tránh khởi tạo lại

  public AStar(int row, int col, int[][] grid) {
    this.row = row;
    this.col = col;
    this.grid = grid;
    cells = new Cell[row][col];
    for (int i = 0; i < row; i++) {
      for (int j = 0; j < col; j++) {
        cells[i][j] = new Cell(i, j);
      }
    }
  }

  private boolean isValid(Point p) {
    return p.x >= 0 && p.x < row && p.y >= 0 && p.y < col;
  }

  protected boolean isUnBlocked(Point p) {
    return grid[p.x][p.y] == 1;
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

  protected double calculateGValue(Cell parent, Cell cell) {
    return cell.g + 1;
  }

  protected Cell getCellAtPoint(Point p) {
    if (isValid(p)) {
      return cells[p.x][p.y];
    }
    return null;
  }

  private List<Cell> get8Neighbours(Cell cell) {
    return Point.EIGHT_NEIGHBOURS.stream()
        .map(p -> getCellAtPoint(cell.add(p)))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private List<Cell> get4Neighbours(Cell cell) {
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

  public List<Point> aStarSearch(Point src, Point dest) {
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

    val beginCell = getCellAtPoint(src);
    beginCell.g = 0;
    beginCell.h = 0;
    beginCell.setParent(beginCell);

    Set<Cell> closed = new HashSet<>();
    Queue<Cell> open = new PriorityQueue<>();
    open.add(beginCell);

    while (!open.isEmpty()) {
      val cell = open.remove();
      closed.add(cell);

      val lastDirX = cell.x - cell.getParent().x;
      val lastDirY = cell.y - cell.getParent().y;
      val nextCellFollowDir = getCellAtPoint(new Point(cell.x + lastDirX, cell.y + lastDirY));
      if (nextCellFollowDir != null) {
        if (isDestination(nextCellFollowDir, dest)) {
          nextCellFollowDir.setParent(cell);
          return getPath(dest);
        } else if (!closed.contains(nextCellFollowDir) && isUnBlocked(nextCellFollowDir)) {
          double gNew = calculateGValue(cell, nextCellFollowDir);
          double hNew = calculateHValue(nextCellFollowDir, dest);
          double fNew = gNew + hNew;
          if (nextCellFollowDir.getF() > fNew) {
            nextCellFollowDir.g = gNew;
            nextCellFollowDir.h = hNew;
            open.add(nextCellFollowDir);
            nextCellFollowDir.setParent(cell);
          }
        }
      }

      val neighbours = getNeighbours(cell);

      for (val neighbour : neighbours) {
        if (neighbour.equals(nextCellFollowDir)) continue;

        if (isDestination(neighbour, dest)) {
          neighbour.setParent(cell);
          return getPath(dest);
        } else if (!closed.contains(neighbour) && isUnBlocked(neighbour)) {
          double gNew = calculateGValue(cell, neighbour);
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
    getPath(dest);
    return Collections.emptyList();
  }

  private List<Cell> getNeighbours(Cell cell) {
    return allowDiagonals ? get8Neighbours(cell) : get4Neighbours(cell);
  }

  @Getter
  @Setter
  public static class Cell extends Point implements Comparable<Cell> {
    Cell parent;
    double g, h;

    public Cell(int x, int y) {
      super(x, y);
      g = h = Double.MAX_VALUE;
    }

    public double getF() {
      return g + h;
    }

    @Override
    public int compareTo(Cell o) {
      return Double.compare(getF(), o.getF());
    }
  }

  @AllArgsConstructor
  @EqualsAndHashCode
  @Getter
  @ToString
  public static class Point {
    public static final Point S = new Point(0, -1);
    public static final Point N = new Point(0, 1);
    public static final Point E = new Point(1, 0);
    public static final Point W = new Point(-1, 0);
    public static final Point SE = new Point(1, -1);
    public static final Point SW = new Point(-1, -1);
    public static final Point NE = new Point(1, 1);
    public static final Point NW = new Point(-1, 1);
    public static final List<Point> EIGHT_NEIGHBOURS =
        Collections.unmodifiableList(Lists.newArrayList(SW, W, NW, N, NE, E, SE, S));
    public static final List<Point> FOUR_NEIGHBOURS =
        Collections.unmodifiableList(Lists.newArrayList(W, N, E, S));
    public int x, y;

    public Point add(Point point) {
      return new Point(x + point.x, y + point.y);
    }
  }
}
