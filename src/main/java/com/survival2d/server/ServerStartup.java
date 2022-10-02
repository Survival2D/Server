package com.survival2d.server;

import com.tvd12.ezyfox.bean.EzyBeanContextBuilder;
import com.tvd12.ezyfoxserver.constant.EzyEventType;
import com.tvd12.ezyfoxserver.context.EzyAppContext;
import com.tvd12.ezyfoxserver.context.EzyPluginContext;
import com.tvd12.ezyfoxserver.embedded.EzyEmbeddedServer;
import com.tvd12.ezyfoxserver.ext.EzyAbstractAppEntryLoader;
import com.tvd12.ezyfoxserver.ext.EzyAbstractPluginEntryLoader;
import com.tvd12.ezyfoxserver.ext.EzyAppEntry;
import com.tvd12.ezyfoxserver.ext.EzyPluginEntry;
import com.tvd12.ezyfoxserver.setting.EzyAppSettingBuilder;
import com.tvd12.ezyfoxserver.setting.EzyPluginSettingBuilder;
import com.tvd12.ezyfoxserver.setting.EzySessionManagementSettingBuilder;
import com.tvd12.ezyfoxserver.setting.EzySettingsBuilder;
import com.tvd12.ezyfoxserver.setting.EzySimpleSettings;
import com.tvd12.ezyfoxserver.setting.EzyUdpSettingBuilder;
import com.tvd12.ezyfoxserver.setting.EzyWebSocketSettingBuilder;
import com.tvd12.ezyfoxserver.setting.EzyZoneSettingBuilder;
import com.tvd12.ezyfoxserver.support.entry.EzySimpleAppEntry;
import com.tvd12.ezyfoxserver.support.entry.EzySimplePluginEntry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerStartup {

  private static final String ZONE_NAME = "survival2d";
  private static final String APP_NAME = "survival2d";
  private static final String PLUGIN_NAME = "survival2d";

  public static void main(String[] args) throws Exception {
    log.trace("Start config server");
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

    EzyWebSocketSettingBuilder webSocketSettingBuilder =
        new EzyWebSocketSettingBuilder().active(true);

    EzyUdpSettingBuilder udpSettingBuilder = new EzyUdpSettingBuilder().active(true);

    EzySessionManagementSettingBuilder sessionManagementSettingBuilder =
        new EzySessionManagementSettingBuilder()
            .sessionMaxRequestPerSecond(
                new EzySessionManagementSettingBuilder.EzyMaxRequestPerSecondBuilder()
                    .value(250)
                    .build());

    EzySimpleSettings settings =
        new EzySettingsBuilder()
            .zone(zoneSettingBuilder.build())
            .websocket(webSocketSettingBuilder.build())
            .udp(udpSettingBuilder.build())
            .sessionManagement(sessionManagementSettingBuilder.build())
            .build();

    EzyEmbeddedServer server = EzyEmbeddedServer.builder().settings(settings).build();

    log.trace("Config complete! Start server...");
    server.start();
    log.trace("Start server complete!");
  }

  public static class Survival2dAppEntry extends EzySimpleAppEntry {

    @Override
    protected String[] getScanableBeanPackages() {
      return new String[]{"com.survival2d.server"};
    }

    @Override
    protected String[] getScanableBindingPackages() {
      return new String[]{"com.survival2d.server"};
    }

    @Override
    protected void setupBeanContext(EzyAppContext context, EzyBeanContextBuilder builder) {
      builder.addProperties("survival2d.yaml");
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
      return new String[]{"com.survival2d.server"};
    }

    @Override
    protected String[] getScanableBindingPackages() {
      return new String[]{"com.survival2d.server"};
    }

    @Override
    protected void setupBeanContext(EzyPluginContext context, EzyBeanContextBuilder builder) {
      builder.addProperties("survival2d.yaml");
    }
  }

  public static class Survival2dPluginEntryLoader extends EzyAbstractPluginEntryLoader {

    @Override
    public EzyPluginEntry load() {
      return new Survival2dPluginEntry();
    }
  }
}
