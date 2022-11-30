// automatically generated by the FlatBuffers compiler, do not modify

package survival2d.flatbuffers;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class PlayerReloadWeaponResponse extends Table {
  public static void ValidateVersion() { Constants.FLATBUFFERS_1_12_0(); }
  public static PlayerReloadWeaponResponse getRootAsPlayerReloadWeaponResponse(ByteBuffer _bb) { return getRootAsPlayerReloadWeaponResponse(_bb, new PlayerReloadWeaponResponse()); }
  public static PlayerReloadWeaponResponse getRootAsPlayerReloadWeaponResponse(ByteBuffer _bb, PlayerReloadWeaponResponse obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
  public PlayerReloadWeaponResponse __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public survival2d.flatbuffers.Weapon weapon() { return weapon(new survival2d.flatbuffers.Weapon()); }
  public survival2d.flatbuffers.Weapon weapon(survival2d.flatbuffers.Weapon obj) { int o = __offset(4); return o != 0 ? obj.__assign(__indirect(o + bb_pos), bb) : null; }

  public static int createPlayerReloadWeaponResponse(FlatBufferBuilder builder,
      int weaponOffset) {
    builder.startTable(1);
    PlayerReloadWeaponResponse.addWeapon(builder, weaponOffset);
    return PlayerReloadWeaponResponse.endPlayerReloadWeaponResponse(builder);
  }

  public static void startPlayerReloadWeaponResponse(FlatBufferBuilder builder) { builder.startTable(1); }
  public static void addWeapon(FlatBufferBuilder builder, int weaponOffset) { builder.addOffset(0, weaponOffset, 0); }
  public static int endPlayerReloadWeaponResponse(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }

    public PlayerReloadWeaponResponse get(int j) { return get(new PlayerReloadWeaponResponse(), j); }
    public PlayerReloadWeaponResponse get(PlayerReloadWeaponResponse obj, int j) {  return obj.__assign(__indirect(__element(j), bb), bb); }
  }
}

