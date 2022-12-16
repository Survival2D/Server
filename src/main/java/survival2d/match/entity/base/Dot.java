package survival2d.match.entity.base;

import lombok.ToString;

@ToString
public final class Dot extends Circle {
  public static final Dot DOT = new Dot();

  public Dot() {
    super(0);
  }
}
