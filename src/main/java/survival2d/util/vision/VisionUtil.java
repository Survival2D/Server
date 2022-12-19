package survival2d.util.vision;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import survival2d.match.config.GameConfig;
import survival2d.match.entity.quadtree.BaseBoundary;
import survival2d.match.entity.quadtree.RectangleBoundary;

public class VisionUtil {
  public static final int VISION_RANGE = 100;

  public static boolean isSameVisionX(Vector2D a, Vector2D b) {
    return Math.abs(Math.floor(a.getX() / VISION_RANGE) - Math.floor(b.getX() / VISION_RANGE))
        < VISION_RANGE;
  }

  public static boolean isSameVisionY(Vector2D a, Vector2D b) {
    return Math.abs(Math.floor(a.getY() / VISION_RANGE) - Math.floor(b.getY() / VISION_RANGE))
        < VISION_RANGE;
  }

  public static BaseBoundary getBoundaryXAxis(Vector2D position) {
    return new RectangleBoundary(
        position.getX() + GameConfig.getInstance().getPlayerViewWidth() / 2 - VISION_RANGE,
        position.getY(),
        VISION_RANGE,
        GameConfig.getInstance().getPlayerViewHeight());
  }

  public static BaseBoundary getBoundaryYAxis(Vector2D position) {
    return new RectangleBoundary(
        position.getX(),
        position.getY() + GameConfig.getInstance().getPlayerViewHeight() / 2 - VISION_RANGE,
        GameConfig.getInstance().getPlayerViewWidth(),
        VISION_RANGE);
  }
}
