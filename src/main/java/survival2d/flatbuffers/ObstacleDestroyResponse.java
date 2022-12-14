// automatically generated by the FlatBuffers compiler, do not modify

package survival2d.flatbuffers;

import com.google.flatbuffers.BaseVector;
import com.google.flatbuffers.Constants;
import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@SuppressWarnings("unused")
public final class ObstacleDestroyResponse extends Table {
  public static void ValidateVersion() {
    Constants.FLATBUFFERS_1_12_0();
  }

  public static ObstacleDestroyResponse getRootAsObstacleDestroyResponse(ByteBuffer _bb) {
    return getRootAsObstacleDestroyResponse(_bb, new ObstacleDestroyResponse());
  }

  public static ObstacleDestroyResponse getRootAsObstacleDestroyResponse(
      ByteBuffer _bb, ObstacleDestroyResponse obj) {
    _bb.order(ByteOrder.LITTLE_ENDIAN);
    return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb));
  }

  public static int createObstacleDestroyResponse(FlatBufferBuilder builder, int id) {
    builder.startTable(1);
    ObstacleDestroyResponse.addId(builder, id);
    return ObstacleDestroyResponse.endObstacleDestroyResponse(builder);
  }

  public static void startObstacleDestroyResponse(FlatBufferBuilder builder) {
    builder.startTable(1);
  }

  public static void addId(FlatBufferBuilder builder, int id) {
    builder.addInt(0, id, 0);
  }

  public static int endObstacleDestroyResponse(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }

  public void __init(int _i, ByteBuffer _bb) {
    __reset(_i, _bb);
  }

  public ObstacleDestroyResponse __assign(int _i, ByteBuffer _bb) {
    __init(_i, _bb);
    return this;
  }

  public int id() {
    int o = __offset(4);
    return o != 0 ? bb.getInt(o + bb_pos) : 0;
  }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) {
      __reset(_vector, _element_size, _bb);
      return this;
    }

    public ObstacleDestroyResponse get(int j) {
      return get(new ObstacleDestroyResponse(), j);
    }

    public ObstacleDestroyResponse get(ObstacleDestroyResponse obj, int j) {
      return obj.__assign(__indirect(__element(j), bb), bb);
    }
  }
}
