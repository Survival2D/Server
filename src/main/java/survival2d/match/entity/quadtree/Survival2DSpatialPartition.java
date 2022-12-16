package survival2d.match.entity.quadtree;

import java.util.Hashtable;

public class Survival2DSpatialPartition extends SpatialPartitionGeneric<BaseMapObject> {
  Survival2DSpatialPartition(Hashtable<Integer, BaseMapObject> objects, QuadTree quadTree) {
    this.objects = objects;
    this.quadTree = quadTree;
  }
}
