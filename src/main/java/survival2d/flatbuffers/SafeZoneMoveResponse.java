// automatically generated by the FlatBuffers compiler, do not modify

package survival2d.flatbuffers;

import java.nio.*;
import java.lang.*;
import java.util.*;
import com.google.flatbuffers.*;

@SuppressWarnings("unused")
public final class SafeZoneMoveResponse extends Table {
  public static void ValidateVersion() { Constants.FLATBUFFERS_1_12_0(); }
  public static SafeZoneMoveResponse getRootAsSafeZoneMoveResponse(ByteBuffer _bb) { return getRootAsSafeZoneMoveResponse(_bb, new SafeZoneMoveResponse()); }
  public static SafeZoneMoveResponse getRootAsSafeZoneMoveResponse(ByteBuffer _bb, SafeZoneMoveResponse obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
  public SafeZoneMoveResponse __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public survival2d.flatbuffers.CircleStruct safeZone() { return safeZone(new survival2d.flatbuffers.CircleStruct()); }
  public survival2d.flatbuffers.CircleStruct safeZone(survival2d.flatbuffers.CircleStruct obj) { int o = __offset(4); return o != 0 ? obj.__assign(o + bb_pos, bb) : null; }

  public static void startSafeZoneMoveResponse(FlatBufferBuilder builder) { builder.startTable(1); }
  public static void addSafeZone(FlatBufferBuilder builder, int safeZoneOffset) { builder.addStruct(0, safeZoneOffset, 0); }
  public static int endSafeZoneMoveResponse(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }

    public SafeZoneMoveResponse get(int j) { return get(new SafeZoneMoveResponse(), j); }
    public SafeZoneMoveResponse get(SafeZoneMoveResponse obj, int j) {  return obj.__assign(__indirect(__element(j), bb), bb); }
  }
}

