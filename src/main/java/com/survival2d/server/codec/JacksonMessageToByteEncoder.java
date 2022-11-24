package com.survival2d.server.codec;

import com.tvd12.ezyfox.codec.EzyMessageByTypeSerializer;
import com.tvd12.ezyfox.codec.EzyObjectToStringEncoder;

public class JacksonMessageToByteEncoder implements EzyObjectToStringEncoder {

    protected final EzyMessageByTypeSerializer serializer;

    public JacksonMessageToByteEncoder(EzyMessageByTypeSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public byte[] encode(Object msg) {
        return serializer.serialize(msg);
    }

    @Override
    public <T> T encode(Object msg, Class<T> outType) {
        return serializer.serialize(msg, outType);
    }
}
