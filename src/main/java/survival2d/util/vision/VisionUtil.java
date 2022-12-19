package survival2d.util.vision;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import survival2d.match.config.GameConfig;
import survival2d.match.entity.quadtree.BaseBoundary;
import survival2d.match.entity.quadtree.RectangleBoundary;

public class VisionUtil {
  public static final int VISION = 100;
  public static final int VISION_PLUS_1 = VISION + 1;
  public static final int VISION_PLUS_2 = VISION + 2;

  private static int getBase(double v) {
    return (int) Math.floor(v / VISION);
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
          getBase(newPos.getX() + GameConfig.getInstance().getHalfPlayerViewWidth()) * VISION - 1,
          getBase(newPos.getY() - GameConfig.getInstance().getHalfPlayerViewHeight()) * VISION - 1,
          VISION_PLUS_2,
          GameConfig.getInstance().getPlayerViewHeightPlus2());
    else
      return new RectangleBoundary(
          getBase(newPos.getX() - GameConfig.getInstance().getHalfPlayerViewWidth()) * VISION - 1,
          getBase(newPos.getY() - GameConfig.getInstance().getHalfPlayerViewHeight()) * VISION - 1,
          VISION_PLUS_2,
          GameConfig.getInstance().getPlayerViewHeightPlus2());
  }

  public static BaseBoundary getBoundaryYAxis(Vector2D oldPos, Vector2D newPos) {
    if (newPos.getY() - oldPos.getY() > 0)
      return new RectangleBoundary(
          getBase(newPos.getX() - GameConfig.getInstance().getHalfPlayerViewWidth()) * VISION - 1,
          getBase(newPos.getY() + GameConfig.getInstance().getHalfPlayerViewHeight()) * VISION - 1,
          GameConfig.getInstance().getPlayerViewWidthPlus2(),
          VISION_PLUS_2);
    else
      return new RectangleBoundary(
          getBase(newPos.getX() - GameConfig.getInstance().getHalfPlayerViewWidth()) * VISION - 1,
          getBase(newPos.getY() - GameConfig.getInstance().getHalfPlayerViewHeight()) * VISION - 1,
          GameConfig.getInstance().getPlayerViewWidthPlus2(),
          VISION_PLUS_2);
  }
}
