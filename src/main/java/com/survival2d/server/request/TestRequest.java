package com.survival2d.server.request;

import com.survival2d.server.match.Vector;
import com.survival2d.server.request.entity.Object;
import com.survival2d.server.request.reader.TestRequestReader;
import com.tvd12.ezyfox.binding.EzyUnmarshaller;
import com.tvd12.ezyfox.binding.annotation.EzyArrayBinding;
import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;
import com.tvd12.ezyfox.binding.annotation.EzyReader;
import com.tvd12.ezyfox.binding.annotation.EzyReaderImpl;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//@EzyObjectBinding
public class TestRequest {

  List<Integer> arr;
  Object obj;
//@EzyReader(TestReader.class)
  List<Test> tests;

//  @EzyObjectBinding
  static class Test {
    String name = "empty";
    Vector position = new Vector(0, 0);
  }

//  @EzyReaderImpl
//  static class TestReader implements com.tvd12.ezyfox.binding.EzyReader<Object, Test> {
//
//    @Override
//    public Test read(EzyUnmarshaller ezyUnmarshaller, Object object) {
//      return new Test();
//    }
//  }
}
