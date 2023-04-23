package survival2d.match.entity.quadtree;

import java.util.Map;
import survival2d.util.serialize.GsonTransient;

public abstract class SpatialPartitionGeneric<T extends Node> {
  @GsonTransient protected Map<Integer, T> objects;
  @GsonTransient protected QuadTree<T> quadTree;
}
