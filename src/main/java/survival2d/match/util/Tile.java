package survival2d.match.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class Tile {

  TileObject type;
  Position position;
}
