package survival2d.match.entity.quadtree;

import java.util.Hashtable;
import survival2d.util.serialize.ExcludeFromGson;

public abstract class SpatialPartitionGeneric<T> {
  @ExcludeFromGson protected Hashtable<Integer, T> objects;
  @ExcludeFromGson protected QuadTree quadTree;
}
