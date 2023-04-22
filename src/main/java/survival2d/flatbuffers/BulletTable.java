// automatically generated by the FlatBuffers compiler, do not modify

package survival2d.flatbuffers;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class BulletTable extends Table {
  public static void ValidateVersion() { Constants.FLATBUFFERS_1_12_0(); }
  public static BulletTable getRootAsBulletTable(ByteBuffer _bb) { return getRootAsBulletTable(_bb, new BulletTable()); }
  public static BulletTable getRootAsBulletTable(ByteBuffer _bb, BulletTable obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
  public BulletTable __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public int id() { int o = __offset(4); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public survival2d.flatbuffers.Vector2Struct position() { return position(new survival2d.flatbuffers.Vector2Struct()); }
  public survival2d.flatbuffers.Vector2Struct position(survival2d.flatbuffers.Vector2Struct obj) { int o = __offset(6); return o != 0 ? obj.__assign(o + bb_pos, bb) : null; }
  public byte type() { int o = __offset(8); return o != 0 ? bb.get(o + bb_pos) : 0; }
  public int owner() { int o = __offset(10); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public survival2d.flatbuffers.Vector2Struct direction() { return direction(new survival2d.flatbuffers.Vector2Struct()); }
  public survival2d.flatbuffers.Vector2Struct direction(survival2d.flatbuffers.Vector2Struct obj) { int o = __offset(12); return o != 0 ? obj.__assign(o + bb_pos, bb) : null; }

  public static void startBulletTable(FlatBufferBuilder builder) { builder.startTable(5); }
  public static void addId(FlatBufferBuilder builder, int id) { builder.addInt(0, id, 0); }
  public static void addPosition(FlatBufferBuilder builder, int positionOffset) { builder.addStruct(1, positionOffset, 0); }
  public static void addType(FlatBufferBuilder builder, byte type) { builder.addByte(2, type, 0); }
  public static void addOwner(FlatBufferBuilder builder, int owner) { builder.addInt(3, owner, 0); }
  public static void addDirection(FlatBufferBuilder builder, int directionOffset) { builder.addStruct(4, directionOffset, 0); }
  public static int endBulletTable(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }

    public BulletTable get(int j) { return get(new BulletTable(), j); }
    public BulletTable get(BulletTable obj, int j) {  return obj.__assign(__indirect(__element(j), bb), bb); }
  }
}

