package survival2d.match.entity.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Circle implements Shape {
  // Không chứa Position do đã nằm trong MapObject rồi

  final double radius;
}
