// automatically generated by the FlatBuffers compiler, do not modify

package survival2d.flatbuffers;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class BulletWithQuantity extends Table {
  public static void ValidateVersion() { Constants.FLATBUFFERS_1_12_0(); }
  public static BulletWithQuantity getRootAsBulletWithQuantity(ByteBuffer _bb) { return getRootAsBulletWithQuantity(_bb, new BulletWithQuantity()); }
  public static BulletWithQuantity getRootAsBulletWithQuantity(ByteBuffer _bb, BulletWithQuantity obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
  public BulletWithQuantity __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public byte type() { int o = __offset(4); return o != 0 ? bb.get(o + bb_pos) : 0; }
  public int num() { int o = __offset(6); return o != 0 ? bb.getInt(o + bb_pos) : 0; }

  public static int createBulletWithQuantity(FlatBufferBuilder builder,
      byte type,
      int num) {
    builder.startTable(2);
    BulletWithQuantity.addNum(builder, num);
    BulletWithQuantity.addType(builder, type);
    return BulletWithQuantity.endBulletWithQuantity(builder);
  }

  public static void startBulletWithQuantity(FlatBufferBuilder builder) { builder.startTable(2); }
  public static void addType(FlatBufferBuilder builder, byte type) { builder.addByte(0, type, 0); }
  public static void addNum(FlatBufferBuilder builder, int num) { builder.addInt(1, num, 0); }
  public static int endBulletWithQuantity(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }

    public BulletWithQuantity get(int j) { return get(new BulletWithQuantity(), j); }
    public BulletWithQuantity get(BulletWithQuantity obj, int j) {  return obj.__assign(__indirect(__element(j), bb), bb); }
  }
}

