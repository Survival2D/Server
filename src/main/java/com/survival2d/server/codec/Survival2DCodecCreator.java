package com.survival2d.server.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.codec.EzyCodecCreator;
import com.tvd12.ezyfox.codec.EzyMessageByTypeSerializer;
import com.tvd12.ezyfox.codec.EzyMessageDeserializer;
import com.tvd12.ezyfox.codec.JacksonSimpleDeserializer;
import com.tvd12.ezyfox.codec.JacksonSimpleSerializer;
import com.tvd12.ezyfox.jackson.JacksonObjectMapperBuilder;

public class Survival2DCodecCreator implements EzyCodecCreator {

  protected final ObjectMapper objectMapper;
  protected final EzyMessageDeserializer deserializer;
  protected final EzyMessageByTypeSerializer serializer;

  public Survival2DCodecCreator() {
    this.objectMapper = newObjectMapper();
    this.serializer = newSerializer();
    this.deserializer = newDeserializer();
  }

  protected ObjectMapper newObjectMapper() {
    return JacksonObjectMapperBuilder.newInstance().build();
  }

  protected EzyMessageDeserializer newDeserializer() {
    return new JacksonSimpleDeserializer(objectMapper);
  }

  protected EzyMessageByTypeSerializer newSerializer() {
    return new JacksonSimpleSerializer(objectMapper);
  }

  @Override
  public Object newEncoder() {
    return new Survival2DMessageToByteEncoder(serializer);
  }

  @Override
  public Object newDecoder(int maxRequestSize) {
    return new Survival2DByteToMessageDecoder(deserializer);
  }
}
