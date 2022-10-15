package com.survival2d.server.util.resource;

import java.io.InputStream;
import lombok.Getter;

public class ResourcesUtil {

  @Getter(lazy = true)
  private static final ResourcesUtil instance = new ResourcesUtil();

  public InputStream getFileFromResourceAsStream(String fileName) {
    ClassLoader classLoader = getClass().getClassLoader();
    return classLoader.getResourceAsStream(fileName);
  }
}
