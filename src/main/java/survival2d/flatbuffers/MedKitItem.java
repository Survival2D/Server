// automatically generated by the FlatBuffers compiler, do not modify

package survival2d.flatbuffers;

import com.google.flatbuffers.BaseVector;
import com.google.flatbuffers.Constants;
import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.Table;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@SuppressWarnings("unused")
public final class MedKitItem extends Table {
  public static void ValidateVersion() {
    Constants.FLATBUFFERS_1_12_0();
  }

  public static MedKitItem getRootAsMedKitItem(ByteBuffer _bb) {
    return getRootAsMedKitItem(_bb, new MedKitItem());
  }

  public static MedKitItem getRootAsMedKitItem(ByteBuffer _bb, MedKitItem obj) {
    _bb.order(ByteOrder.LITTLE_ENDIAN);
    return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb));
  }

  public static void startMedKitItem(FlatBufferBuilder builder) {
    builder.startTable(0);
  }

  public static int endMedKitItem(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }

  public void __init(int _i, ByteBuffer _bb) {
    __reset(_i, _bb);
  }

  public MedKitItem __assign(int _i, ByteBuffer _bb) {
    __init(_i, _bb);
    return this;
  }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) {
      __reset(_vector, _element_size, _bb);
      return this;
    }

    public MedKitItem get(int j) {
      return get(new MedKitItem(), j);
    }

    public MedKitItem get(MedKitItem obj, int j) {
      return obj.__assign(__indirect(__element(j), bb), bb);
    }
  }
}
