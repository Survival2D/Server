package survival2d.match.entity.quadtree;

import java.util.Hashtable;

public abstract class SpatialPartitionGeneric<T> {

  Hashtable<Integer, T> objects;
  QuadTree quadTree;
}
