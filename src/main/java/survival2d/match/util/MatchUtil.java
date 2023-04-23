package survival2d.match.util;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MatchUtil {

  public static Vector2 randomPosition(float fromX, float toX, float fromY, float toY) {
    return new Vector2(MathUtils.random(fromX, toX), MathUtils.random(fromY, toY));
  }

  public static boolean isIntersect(Shape2D s1, Shape2D s2) {
    if (s1 instanceof Circle c1) {
      if (s2 instanceof Circle c2) {
        return Intersector.overlaps(c1, c2);
      } else if (s2 instanceof Rectangle r2) {
        return Intersector.overlaps(c1, r2);
      }
    } else if (s1 instanceof Rectangle r1) {
      if (s2 instanceof Circle c2) {
        return Intersector.overlaps(c2, r1);
      } else if (s2 instanceof Rectangle r2) {
        return Intersector.overlaps(r1, r2);
      }
    }
    log.error(
        "Not handled collision between {} and {}",
        s1.getClass().getSimpleName(),
        s2.getClass().getSimpleName());
    return false;
  }
}
