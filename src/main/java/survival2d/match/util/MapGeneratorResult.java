package survival2d.match.util;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class MapGeneratorResult {

  List<Tile> mapObjects;
}
