// automatically generated by the FlatBuffers compiler, do not modify

package survival2d.flatbuffers;

import com.google.flatbuffers.BaseVector;
import com.google.flatbuffers.Constants;
import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@SuppressWarnings("unused")
public final class Hand extends Table {
  public static void ValidateVersion() {
    Constants.FLATBUFFERS_1_12_0();
  }

  public static Hand getRootAsHand(ByteBuffer _bb) {
    return getRootAsHand(_bb, new Hand());
  }

  public static Hand getRootAsHand(ByteBuffer _bb, Hand obj) {
    _bb.order(ByteOrder.LITTLE_ENDIAN);
    return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb));
  }

  public static void startHand(FlatBufferBuilder builder) {
    builder.startTable(0);
  }

  public static int endHand(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }

  public void __init(int _i, ByteBuffer _bb) {
    __reset(_i, _bb);
  }

  public Hand __assign(int _i, ByteBuffer _bb) {
    __init(_i, _bb);
    return this;
  }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) {
      __reset(_vector, _element_size, _bb);
      return this;
    }

    public Hand get(int j) {
      return get(new Hand(), j);
    }

    public Hand get(Hand obj, int j) {
      return obj.__assign(__indirect(__element(j), bb), bb);
    }
  }
}
