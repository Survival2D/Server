// automatically generated by the FlatBuffers compiler, do not modify

package survival2d.flatbuffers;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class PlayerChangeWeaponResponse extends Table {
  public static void ValidateVersion() { Constants.FLATBUFFERS_1_12_0(); }
  public static PlayerChangeWeaponResponse getRootAsPlayerChangeWeaponResponse(ByteBuffer _bb) { return getRootAsPlayerChangeWeaponResponse(_bb, new PlayerChangeWeaponResponse()); }
  public static PlayerChangeWeaponResponse getRootAsPlayerChangeWeaponResponse(ByteBuffer _bb, PlayerChangeWeaponResponse obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
  public PlayerChangeWeaponResponse __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public String username() { int o = __offset(4); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer usernameAsByteBuffer() { return __vector_as_bytebuffer(4, 1); }
  public ByteBuffer usernameInByteBuffer(ByteBuffer _bb) { return __vector_in_bytebuffer(_bb, 4, 1); }
  public byte slot() { int o = __offset(6); return o != 0 ? bb.get(o + bb_pos) : 0; }

  public static int createPlayerChangeWeaponResponse(FlatBufferBuilder builder,
      int usernameOffset,
      byte slot) {
    builder.startTable(2);
    PlayerChangeWeaponResponse.addUsername(builder, usernameOffset);
    PlayerChangeWeaponResponse.addSlot(builder, slot);
    return PlayerChangeWeaponResponse.endPlayerChangeWeaponResponse(builder);
  }

  public static void startPlayerChangeWeaponResponse(FlatBufferBuilder builder) { builder.startTable(2); }
  public static void addUsername(FlatBufferBuilder builder, int usernameOffset) { builder.addOffset(0, usernameOffset, 0); }
  public static void addSlot(FlatBufferBuilder builder, byte slot) { builder.addByte(1, slot, 0); }
  public static int endPlayerChangeWeaponResponse(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }

    public PlayerChangeWeaponResponse get(int j) { return get(new PlayerChangeWeaponResponse(), j); }
    public PlayerChangeWeaponResponse get(PlayerChangeWeaponResponse obj, int j) {  return obj.__assign(__indirect(__element(j), bb), bb); }
  }
}

