package com.survival2d.server.codec;

import com.tvd12.ezyfox.codec.EzyMessageDeserializer;
import com.tvd12.ezyfox.codec.EzyStringToObjectDecoder;

public class JacksonByteToMessageDecoder implements EzyStringToObjectDecoder {

    private final EzyMessageDeserializer deserializer;

    public JacksonByteToMessageDecoder(EzyMessageDeserializer deserializer) {
        this.deserializer = deserializer;
    }

    @Override
    public Object decode(String bytes) {
        return deserializer.deserialize(bytes);
    }

    @Override
    public Object decode(byte[] bytes) {
        return deserializer.deserialize(bytes);
    }
}
