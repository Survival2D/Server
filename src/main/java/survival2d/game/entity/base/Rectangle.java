package survival2d.game.entity.base;

import lombok.ToString;
import lombok.Value;

@Value
@ToString
public class Rectangle implements Shape {

  double width;
  double height;
}
