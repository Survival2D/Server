package com.survival2d.server.request.reader;

import com.survival2d.server.request.TestRequest;
import com.tvd12.ezyfox.binding.EzyReader;
import com.tvd12.ezyfox.binding.EzyUnmarshaller;

// @EzyReaderImpl
public class TestRequestReader implements EzyReader<Object, TestRequest> {

  @Override
  public TestRequest read(EzyUnmarshaller ezyUnmarshaller, Object o) {
    return new TestRequest();
  }
}
