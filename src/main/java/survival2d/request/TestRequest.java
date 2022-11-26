package survival2d.request;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.math.Vector2D;


@Getter
@Setter
// @EzyObjectBinding
public class TestRequest {

  List<Integer> arr;
  //  TempObject obj;
  // @EzyReader(TestReader.class)
  List<Test> tests;

  //  @EzyObjectBinding
  static class Test {

    String name = "empty";
    Vector2D position = new Vector2D(0, 0);
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
