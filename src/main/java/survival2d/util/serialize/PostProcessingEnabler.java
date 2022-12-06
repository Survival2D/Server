package survival2d.util.serialize;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import lombok.Getter;

public class PostProcessingEnabler implements TypeAdapterFactory {
  @Getter(lazy = true)
  private static final PostProcessingEnabler instance = new PostProcessingEnabler();

  @Override
  public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
    TypeAdapter<T> delegate = gson.getDelegateAdapter(this, typeToken);
    return new TypeAdapter<T>() {
      @Override
      public void write(JsonWriter jsonWriter, T t) throws IOException {
        delegate.write(jsonWriter, t);
      }

      @Override
      public T read(JsonReader jsonReader) throws IOException {
        T result = delegate.read(jsonReader);
        if (result instanceof PostProcessable) {
          ((PostProcessable) result).postProcess();
        }
        return result;
      }
    };
  }
}
