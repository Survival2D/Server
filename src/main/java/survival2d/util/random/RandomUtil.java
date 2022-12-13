package survival2d.util.random;

public class RandomUtil {
  public static double random(double from, double to) {
    return Math.random() * (to - from) + from;
  }
}
