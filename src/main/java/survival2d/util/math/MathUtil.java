package survival2d.util.math;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import survival2d.match.entity.base.Circle;
import survival2d.match.entity.base.Rectangle;
import survival2d.match.entity.base.Shape;
import survival2d.util.random.RandomUtil;

@Slf4j
public class MathUtil {

  public static Vector2D randomPosition(double fromX, double toX, double fromY, double toY) {
    return new Vector2D(RandomUtil.random(fromX, toX), RandomUtil.random(fromY, toY));
  }

  public static Vector2D random(double width, double height) {
    return new Vector2D(Math.random() * width, Math.random() * height);
  }

  public static boolean isZero(Vector2D vector) {
    return vector.equals(Vector2D.ZERO);
  }

  public static boolean isIntersect(Vector2D v1, Shape s1, Vector2D v2, Shape s2) {
    if (s1 instanceof Circle) {
      if (s2 instanceof Circle) {
        return isIntersectBetweenCircleAndCircle(v1, (Circle) s1, v2, (Circle) s2);
      } else if (s2 instanceof Rectangle) {
        return isIntersectBetweenCircleAndRectangle(v1, (Circle) s1, v2, (Rectangle) s2);
      }
    } else if (s1 instanceof Rectangle) {
      if (s2 instanceof Circle) {
        return isIntersectBetweenCircleAndRectangle(v2, (Circle) s2, v1, (Rectangle) s1);
      } else if (s2 instanceof Rectangle) {
        return isIntersectBetweenRectangleAndRectangle(v1, (Rectangle) s1, v2, (Rectangle) s2);
      }
    }
    log.error("Not handled collision between {} and {}", s1.getClass(), s2.getClass());
    return false;
  }

  public static boolean isIntersectBetweenCircleAndCircle(
      Vector2D v1, Circle c1, Vector2D v2, Circle c2) {
    return v1.distance(v2) <= c1.getRadius() + c2.getRadius();
  }

  public static boolean isIntersectBetweenCircleAndRectangle(
      Vector2D v1, Circle c1, Vector2D v2, Rectangle r2) {
    val dx = Math.abs(v1.getX() - (v2.getX() + r2.getWidth() / 2));
    val dy = Math.abs(v1.getY() - (v2.getY() + r2.getHeight() / 2));
    if (dx > c1.getRadius() + r2.getWidth() / 2) {
      return false;
    }
    if (dy > c1.getRadius() + r2.getHeight() / 2) {
      return false;
    }
    if (dx <= r2.getWidth() / 2) {
      return true;
    }
    if (dy <= r2.getHeight() / 2) {
      return true;
    }
    val dCorner_2 =
        (dx - r2.getWidth() / 2) * (dx - r2.getWidth() / 2)
            + (dy - r2.getHeight() / 2) * (dy - r2.getHeight() / 2);
    return dCorner_2 <= c1.getRadius() * c1.getRadius();
  }

  public static boolean isIntersectBetweenRectangleAndRectangle(
      Vector2D v1, Rectangle r1, Vector2D v2, Rectangle r2) {
    return v1.getX() <= v2.getX() + r2.getWidth()
        && v1.getX() + r1.getWidth() >= v2.getX()
        && v1.getY() <= v2.getY() + r2.getHeight()
        && v1.getY() + r1.getHeight() >= v2.getY();
  }
}
