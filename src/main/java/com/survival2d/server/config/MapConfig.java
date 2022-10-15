package com.survival2d.server.config;

import com.survival2d.server.util.config.ConfigReader;

public class MapConfig {

  private static final String CONFIG_FILE = "map.json";
  private double width;
  private double height;

  public static void load() {
    InstanceHolder.instance = ConfigReader.fromFile(CONFIG_FILE, MapConfig.class);
  }

  public static MapConfig getInstance() {
    return InstanceHolder.instance;
  }

  private static class InstanceHolder {

    private static MapConfig instance;

    static {
      load();
    }
  }
}
