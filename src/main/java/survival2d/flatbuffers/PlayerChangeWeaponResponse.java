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


  public static void startPlayerChangeWeaponResponse(FlatBufferBuilder builder) { builder.startTable(0); }
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

