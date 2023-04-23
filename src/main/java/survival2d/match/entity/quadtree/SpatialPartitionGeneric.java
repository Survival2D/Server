package survival2d.match.entity.quadtree;

import java.util.Map;
import survival2d.util.serialize.ExcludeFromGson;

public abstract class SpatialPartitionGeneric<T extends Node> {
  @ExcludeFromGson protected Map<Integer, T> objects;
  @ExcludeFromGson protected QuadTree<T> quadTree;
}
