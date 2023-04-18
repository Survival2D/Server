package survival2d.util.serialize;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class GsonTransientExclusionStrategy implements ExclusionStrategy {

  public static GsonTransientExclusionStrategy getInstance() {
    return InstanceHolder.instance;
  }

  @Override
  public boolean shouldSkipField(FieldAttributes fieldAttributes) {
    return fieldAttributes.getAnnotation(GsonTransient.class) != null;
  }

  @Override
  public boolean shouldSkipClass(Class<?> aClass) {
    return aClass.getAnnotation(GsonTransient.class) != null;
  }

  private static class InstanceHolder {

    private static final GsonTransientExclusionStrategy instance =
        new GsonTransientExclusionStrategy();
  }
}
