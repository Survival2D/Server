package survival2d.util.vision;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import survival2d.match.config.GameConfig;
import survival2d.match.entity.quadtree.BaseBoundary;
import survival2d.match.entity.quadtree.RectangleBoundary;

public class VisionUtil {
  public static final int TILE_SIZE = 100;
  public static final int HALF_TILE_SIZE = TILE_SIZE / 2;
  public static final int TILE_SIZE_PLUS_1 = TILE_SIZE + 1;
  public static final int TILE_SIZE_PLUS_2 = TILE_SIZE + 2;

  private static int getBase(double v) {
    return (int) Math.floor(v / TILE_SIZE);
  }

  public static int getBaseX(Vector2D v) {
    return getBase(v.getX());
  }

  public static int getBaseY(Vector2D v) {
    return getBase(v.getY());
  }

  public static boolean isSameVisionX(Vector2D a, Vector2D b) {
    return getBaseX(a) == getBaseX(b);
  }

  public static boolean isSameVisionY(Vector2D a, Vector2D b) {
    return getBaseY(a) == getBaseY(b);
  }

  public static BaseBoundary getBoundaryXAxis(Vector2D oldPos, Vector2D newPos) {
    if (newPos.getX() - oldPos.getX() > 0)
      return new RectangleBoundary(
          getBase(newPos.getX() + GameConfig.getInstance().getHalfPlayerViewWidth()) * TILE_SIZE - 1,
          getBase(newPos.getY() - GameConfig.getInstance().getHalfPlayerViewHeight()) * TILE_SIZE - 1,
          TILE_SIZE_PLUS_2,
          GameConfig.getInstance().getPlayerViewHeightPlus2());
    else
      return new RectangleBoundary(
          getBase(newPos.getX() - GameConfig.getInstance().getHalfPlayerViewWidth()) * TILE_SIZE - 1,
          getBase(newPos.getY() - GameConfig.getInstance().getHalfPlayerViewHeight()) * TILE_SIZE - 1,
          TILE_SIZE_PLUS_2,
          GameConfig.getInstance().getPlayerViewHeightPlus2());
  }

  public static BaseBoundary getBoundaryYAxis(Vector2D oldPos, Vector2D newPos) {
    if (newPos.getY() - oldPos.getY() > 0)
      return new RectangleBoundary(
          getBase(newPos.getX() - GameConfig.getInstance().getHalfPlayerViewWidth()) * TILE_SIZE - 1,
          getBase(newPos.getY() + GameConfig.getInstance().getHalfPlayerViewHeight()) * TILE_SIZE - 1,
          GameConfig.getInstance().getPlayerViewWidthPlus2(),
          TILE_SIZE_PLUS_2);
    else
      return new RectangleBoundary(
          getBase(newPos.getX() - GameConfig.getInstance().getHalfPlayerViewWidth()) * TILE_SIZE - 1,
          getBase(newPos.getY() - GameConfig.getInstance().getHalfPlayerViewHeight()) * TILE_SIZE - 1,
          GameConfig.getInstance().getPlayerViewWidthPlus2(),
          TILE_SIZE_PLUS_2);
  }
}
