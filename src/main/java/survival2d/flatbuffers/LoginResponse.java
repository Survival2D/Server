// automatically generated by the FlatBuffers compiler, do not modify

package survival2d.flatbuffers;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class LoginResponse extends Table {
  public static void ValidateVersion() { Constants.FLATBUFFERS_1_12_0(); }
  public static LoginResponse getRootAsLoginResponse(ByteBuffer _bb) { return getRootAsLoginResponse(_bb, new LoginResponse()); }
  public static LoginResponse getRootAsLoginResponse(ByteBuffer _bb, LoginResponse obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
  public LoginResponse __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public int playerId() { int o = __offset(4); return o != 0 ? bb.getInt(o + bb_pos) : 0; }

  public static int createLoginResponse(FlatBufferBuilder builder,
      int playerId) {
    builder.startTable(1);
    LoginResponse.addPlayerId(builder, playerId);
    return LoginResponse.endLoginResponse(builder);
  }

  public static void startLoginResponse(FlatBufferBuilder builder) { builder.startTable(1); }
  public static void addPlayerId(FlatBufferBuilder builder, int playerId) { builder.addInt(0, playerId, 0); }
  public static int endLoginResponse(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }

    public LoginResponse get(int j) { return get(new LoginResponse(), j); }
    public LoginResponse get(LoginResponse obj, int j) {  return obj.__assign(__indirect(__element(j), bb), bb); }
  }
}

