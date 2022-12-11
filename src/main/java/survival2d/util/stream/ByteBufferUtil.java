package survival2d.util.stream;

import java.nio.ByteBuffer;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
public class ByteBufferUtil {
  private static final int EZY_FOX_HEADER_SIZE = 1;
  private static final byte EZY_FOX_HEADER = 0b00010000; // Header bypass EzyFox check isRawBytes

  public static ByteBuffer ezyFoxBytesToByteBuffer(byte[] bytes) {
    val data = Arrays.copyOfRange(bytes, EZY_FOX_HEADER_SIZE, bytes.length);
    return ByteBuffer.wrap(data);
  }

  public static byte[] byteBufferToEzyFoxBytes(ByteBuffer byteBuffer) {
    val remaining = byteBuffer.remaining();
    val bytes = new byte[remaining + 1];
    bytes[0] = EZY_FOX_HEADER;
    byteBuffer.get(bytes, EZY_FOX_HEADER_SIZE, remaining);
    return bytes;
  }
}
