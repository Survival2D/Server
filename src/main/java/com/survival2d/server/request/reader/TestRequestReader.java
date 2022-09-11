package com.survival2d.server.request.reader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.survival2d.server.request.TestRequest;
import com.tvd12.ezyfox.binding.EzyReader;
import com.tvd12.ezyfox.binding.EzyUnmarshaller;
import com.tvd12.ezyfox.binding.annotation.EzyReaderImpl;
import com.tvd12.ezyfox.entity.EzyHashMap;

@EzyReaderImpl
public class TestRequestReader implements EzyReader<Object, TestRequest> {

  @Override
  public TestRequest read(EzyUnmarshaller ezyUnmarshaller, Object o) {
//    try {
//      String content = o.toString();
//      return new ObjectMapper().readValue(content, TestRequest.class);
//    } catch (JsonProcessingException e) {
//      throw new RuntimeException(e);
//    }
    return new Gson().fromJson(o.toString(),TestRequest.class);
  }
}
