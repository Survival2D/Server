package survival2d.match.util;

import lombok.Getter;

@Getter
public class Rectangle extends Position {
  public int width;
  public int height;

  public Rectangle(int x, int y, int width, int height) {
    super(x, y);
    this.width = width;
    this.height = height;
  }
}
