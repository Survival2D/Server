package survival2d.util.serialize;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class ExcludeAnnotationFromGsonStrategy implements ExclusionStrategy {
  public static ExcludeAnnotationFromGsonStrategy getInstance() {
    return InstanceHolder.instance;
  }

  @Override
  public boolean shouldSkipField(FieldAttributes fieldAttributes) {
    return fieldAttributes.getAnnotation(ExcludeFromGson.class) != null;
  }

  @Override
  public boolean shouldSkipClass(Class<?> aClass) {
    return aClass.getAnnotation(ExcludeFromGson.class) != null;
  }

  private static class InstanceHolder {
    private static final ExcludeAnnotationFromGsonStrategy instance =
        new ExcludeAnnotationFromGsonStrategy();
  }
}
