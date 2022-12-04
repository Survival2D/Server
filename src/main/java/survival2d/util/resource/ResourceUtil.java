package survival2d.util.resource;

import java.io.InputStream;
import lombok.Getter;

public class ResourceUtil {

  @Getter(lazy = true)
  private static final ResourceUtil instance = new ResourceUtil();

  public InputStream getFileFromResourceAsStream(String fileName) {
    ClassLoader classLoader = getClass().getClassLoader();
    return classLoader.getResourceAsStream(fileName);
  }
}
