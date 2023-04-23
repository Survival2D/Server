package survival2d.util.serialize;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;

public class GsonHolder {

  @Getter(lazy = true)
  private static final Gson normalGson = new Gson();

  @Getter(lazy = true)
  private static final Gson withExcludeAnnotation =
      new GsonBuilder()
          .setExclusionStrategies(GsonTransientExclusionStrategy.getInstance())
          .create();

  @Getter(lazy = true)
  private static final Gson enablePostProcess =
      new GsonBuilder().registerTypeAdapterFactory(PostProcessingEnabler.getInstance()).create();
}
