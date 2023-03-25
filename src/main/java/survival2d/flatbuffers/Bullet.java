// automatically generated by the FlatBuffers compiler, do not modify

package survival2d.flatbuffers;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class Bullet extends Table {
  public static void ValidateVersion() { Constants.FLATBUFFERS_1_12_0(); }
  public static Bullet getRootAsBullet(ByteBuffer _bb) { return getRootAsBullet(_bb, new Bullet()); }
  public static Bullet getRootAsBullet(ByteBuffer _bb, Bullet obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
  public Bullet __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public int id() { int o = __offset(4); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public survival2d.flatbuffers.Vec2 position() { return position(new survival2d.flatbuffers.Vec2()); }
  public survival2d.flatbuffers.Vec2 position(survival2d.flatbuffers.Vec2 obj) { int o = __offset(6); return o != 0 ? obj.__assign(o + bb_pos, bb) : null; }
  public byte type() { int o = __offset(8); return o != 0 ? bb.get(o + bb_pos) : 0; }
  public String owner() { int o = __offset(10); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer ownerAsByteBuffer() { return __vector_as_bytebuffer(10, 1); }
  public ByteBuffer ownerInByteBuffer(ByteBuffer _bb) { return __vector_in_bytebuffer(_bb, 10, 1); }
  public survival2d.flatbuffers.Vec2 direction() { return direction(new survival2d.flatbuffers.Vec2()); }
  public survival2d.flatbuffers.Vec2 direction(survival2d.flatbuffers.Vec2 obj) { int o = __offset(12); return o != 0 ? obj.__assign(o + bb_pos, bb) : null; }

  public static void startBullet(FlatBufferBuilder builder) { builder.startTable(5); }
  public static void addId(FlatBufferBuilder builder, int id) { builder.addInt(0, id, 0); }
  public static void addPosition(FlatBufferBuilder builder, int positionOffset) { builder.addStruct(1, positionOffset, 0); }
  public static void addType(FlatBufferBuilder builder, byte type) { builder.addByte(2, type, 0); }
  public static void addOwner(FlatBufferBuilder builder, int ownerOffset) { builder.addOffset(3, ownerOffset, 0); }
  public static void addDirection(FlatBufferBuilder builder, int directionOffset) { builder.addStruct(4, directionOffset, 0); }
  public static int endBullet(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }

    public Bullet get(int j) { return get(new Bullet(), j); }
    public Bullet get(Bullet obj, int j) {  return obj.__assign(__indirect(__element(j), bb), bb); }
  }
}

