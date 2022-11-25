package com.survival2d.server.codec;

import com.tvd12.ezyfox.codec.EzyMessageByTypeSerializer;
import com.tvd12.ezyfox.codec.EzyObjectToStringEncoder;

public class Survival2DMessageToByteEncoder implements EzyObjectToStringEncoder {

  protected final EzyMessageByTypeSerializer serializer;

  public Survival2DMessageToByteEncoder(EzyMessageByTypeSerializer serializer) {
    this.serializer = serializer;
  }

  @Override
  public byte[] encode(Object msg) {
    return (byte[]) msg;
  }

  @Override
  public <T> T encode(Object msg, Class<T> outType) {
    return serializer.serialize(msg, outType);
  }
}
