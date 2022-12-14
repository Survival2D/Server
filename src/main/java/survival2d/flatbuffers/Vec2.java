// automatically generated by the FlatBuffers compiler, do not modify

package survival2d.flatbuffers;

import com.google.flatbuffers.BaseVector;
import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Struct;
import java.nio.ByteBuffer;

@SuppressWarnings("unused")
public final class Vec2 extends Struct {
  public static int createVec2(FlatBufferBuilder builder, double x, double y) {
    builder.prep(8, 16);
    builder.putDouble(y);
    builder.putDouble(x);
    return builder.offset();
  }

  public void __init(int _i, ByteBuffer _bb) {
    __reset(_i, _bb);
  }

  public Vec2 __assign(int _i, ByteBuffer _bb) {
    __init(_i, _bb);
    return this;
  }

  public double x() {
    return bb.getDouble(bb_pos + 0);
  }

  public double y() {
    return bb.getDouble(bb_pos + 8);
  }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) {
      __reset(_vector, _element_size, _bb);
      return this;
    }

    public Vec2 get(int j) {
      return get(new Vec2(), j);
    }

    public Vec2 get(Vec2 obj, int j) {
      return obj.__assign(__element(j), bb);
    }
  }
}
