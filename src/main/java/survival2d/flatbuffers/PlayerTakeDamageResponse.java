// automatically generated by the FlatBuffers compiler, do not modify

package survival2d.flatbuffers;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class PlayerTakeDamageResponse extends Table {
  public static void ValidateVersion() { Constants.FLATBUFFERS_1_12_0(); }
  public static PlayerTakeDamageResponse getRootAsPlayerTakeDamageResponse(ByteBuffer _bb) { return getRootAsPlayerTakeDamageResponse(_bb, new PlayerTakeDamageResponse()); }
  public static PlayerTakeDamageResponse getRootAsPlayerTakeDamageResponse(ByteBuffer _bb, PlayerTakeDamageResponse obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
  public PlayerTakeDamageResponse __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public int playerId() { int o = __offset(4); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public double remainHp() { int o = __offset(6); return o != 0 ? bb.getDouble(o + bb_pos) : 0.0; }

  public static int createPlayerTakeDamageResponse(FlatBufferBuilder builder,
      int playerId,
      double remainHp) {
    builder.startTable(2);
    PlayerTakeDamageResponse.addRemainHp(builder, remainHp);
    PlayerTakeDamageResponse.addPlayerId(builder, playerId);
    return PlayerTakeDamageResponse.endPlayerTakeDamageResponse(builder);
  }

  public static void startPlayerTakeDamageResponse(FlatBufferBuilder builder) { builder.startTable(2); }
  public static void addPlayerId(FlatBufferBuilder builder, int playerId) { builder.addInt(0, playerId, 0); }
  public static void addRemainHp(FlatBufferBuilder builder, double remainHp) { builder.addDouble(1, remainHp, 0.0); }
  public static int endPlayerTakeDamageResponse(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }

    public PlayerTakeDamageResponse get(int j) { return get(new PlayerTakeDamageResponse(), j); }
    public PlayerTakeDamageResponse get(PlayerTakeDamageResponse obj, int j) {  return obj.__assign(__indirect(__element(j), bb), bb); }
  }
}

