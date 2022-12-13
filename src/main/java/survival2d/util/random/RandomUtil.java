package survival2d.util.random;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {
  public static double random(double from, double to) {
    return ThreadLocalRandom.current().nextDouble() * (to - from) + from;
  }
}
