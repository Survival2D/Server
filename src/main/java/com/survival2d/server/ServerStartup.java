package com.survival2d.server;

import com.tvd12.ezyfox.bean.annotation.EzyPropertiesSources;
import com.tvd12.ezyfoxserver.constant.EzyEventType;
import com.tvd12.ezyfoxserver.embedded.EzyEmbeddedServer;
import com.tvd12.ezyfoxserver.ext.EzyAbstractAppEntryLoader;
import com.tvd12.ezyfoxserver.ext.EzyAbstractPluginEntryLoader;
import com.tvd12.ezyfoxserver.ext.EzyAppEntry;
import com.tvd12.ezyfoxserver.ext.EzyPluginEntry;
import com.tvd12.ezyfoxserver.setting.EzyAppSettingBuilder;
import com.tvd12.ezyfoxserver.setting.EzyPluginSettingBuilder;
import com.tvd12.ezyfoxserver.setting.EzySettingsBuilder;
import com.tvd12.ezyfoxserver.setting.EzySimpleSettings;
import com.tvd12.ezyfoxserver.setting.EzyZoneSettingBuilder;
import com.tvd12.ezyfoxserver.support.entry.EzySimpleAppEntry;
import com.tvd12.ezyfoxserver.support.entry.EzySimplePluginEntry;

@EzyPropertiesSources({"survival2d.properties"})
public class ServerStartup {

  private static final String ZONE_NAME = "survival2d";
  private static final String APP_NAME = "survival2d";
  private static final String PLUGIN_NAME = "survival2d";

  public static void main(String[] args) throws Exception {
    EzyPluginSettingBuilder pluginSettingBuilder =
        new EzyPluginSettingBuilder()
            .name(PLUGIN_NAME)
            .addListenEvent(EzyEventType.USER_LOGIN)
            .entryLoader(Survival2dPluginEntryLoader.class);

    EzyAppSettingBuilder appSettingBuilder =
        new EzyAppSettingBuilder().name(APP_NAME).entryLoader(Survival2dAppEntryLoader.class);

    EzyZoneSettingBuilder zoneSettingBuilder =
        new EzyZoneSettingBuilder()
            .name(ZONE_NAME)
            .plugin(pluginSettingBuilder.build())
            .application(appSettingBuilder.build());

    EzySimpleSettings settings = new EzySettingsBuilder().zone(zoneSettingBuilder.build()).build();

    EzyEmbeddedServer server = EzyEmbeddedServer.builder().settings(settings).build();

    server.start();
  }

  public static class Survival2dAppEntry extends EzySimpleAppEntry {

    @Override
    protected String[] getScanableBeanPackages() {
      return new String[] {"com.survival2d.server"};
    }

    @Override
    protected String[] getScanableBindingPackages() {
      return new String[] {"com.survival2d.server"};
    }
  }

  public static class Survival2dAppEntryLoader extends EzyAbstractAppEntryLoader {

    @Override
    public EzyAppEntry load() {
      return new Survival2dAppEntry();
    }
  }

  public static class Survival2dPluginEntry extends EzySimplePluginEntry {

    @Override
    protected String[] getScanableBeanPackages() {
      return new String[] {"com.survival2d.server"};
    }

    @Override
    protected String[] getScanableBindingPackages() {
      return new String[] {"com.survival2d.server"};
    }
  }

  public static class Survival2dPluginEntryLoader extends EzyAbstractPluginEntryLoader {

    @Override
    public EzyPluginEntry load() {
      return new Survival2dPluginEntry();
    }
  }
}
