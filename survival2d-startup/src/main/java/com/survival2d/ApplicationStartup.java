package com.survival2d;

import com.survival2d.app.AppEntry;
import com.survival2d.app.AppEntryLoader;
import com.survival2d.plugin.PluginEntry;
import com.survival2d.plugin.PluginEntryLoader;
import com.tvd12.ezyfoxserver.constant.EzyEventType;
import com.tvd12.ezyfoxserver.embedded.EzyEmbeddedServer;
import com.tvd12.ezyfoxserver.ext.EzyAppEntry;
import com.tvd12.ezyfoxserver.ext.EzyPluginEntry;
import com.tvd12.ezyfoxserver.setting.EzyAppSetting;
import com.tvd12.ezyfoxserver.setting.EzyAppSettingBuilder;
import com.tvd12.ezyfoxserver.setting.EzyPluginSetting;
import com.tvd12.ezyfoxserver.setting.EzyPluginSettingBuilder;
import com.tvd12.ezyfoxserver.setting.EzySessionManagementSettingBuilder;
import com.tvd12.ezyfoxserver.setting.EzySettingsBuilder;
import com.tvd12.ezyfoxserver.setting.EzySimpleSettings;
import com.tvd12.ezyfoxserver.setting.EzyUdpSettingBuilder;
import com.tvd12.ezyfoxserver.setting.EzyWebSocketSettingBuilder;
import com.tvd12.ezyfoxserver.setting.EzyZoneSettingBuilder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ApplicationStartup {

  public static final String ZONE_APP_NAME = "survival2d";

  public static void main(String[] args) throws Exception {

    EzyPluginSettingBuilder pluginSettingBuilder =
        new EzyPluginSettingBuilder()
            .name(ZONE_APP_NAME)
            .addListenEvent(EzyEventType.USER_LOGIN)
            .entryLoader(PluginEntryLoader.class);

    EzyAppSettingBuilder appSettingBuilder =
        new EzyAppSettingBuilder().name(ZONE_APP_NAME).entryLoader(DecoratedAppEntryLoader.class);

    EzyZoneSettingBuilder zoneSettingBuilder =
        new EzyZoneSettingBuilder()
            .name(ZONE_APP_NAME)
            .application(appSettingBuilder.build())
            .plugin(pluginSettingBuilder.build());

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
    server.start();
  }

//  public static class DecoratedPluginEntryLoader extends PluginEntryLoader {
//
//    @Override
//    public EzyPluginEntry load() throws Exception {
//      return new PluginEntry() {
//
//        @Override
//        protected String getConfigFile(EzyPluginSetting setting) {
//          return Paths.get(getPluginPath(setting), "config", "config.properties").toString();
//        }
//
//        private String getPluginPath(EzyPluginSetting setting) {
//          Path pluginPath = Paths.get("survival2d-plugin");
//          if (!Files.exists(pluginPath)) {
//            pluginPath = Paths.get("../survival2d-plugin");
//          }
//          return pluginPath.toString();
//        }
//      };
//    }
//  }

  public static class DecoratedAppEntryLoader extends AppEntryLoader {

    @Override
    public EzyAppEntry load() throws Exception {
      return new AppEntry() {

        @Override
        protected String getConfigFile(EzyAppSetting setting) {
          return Paths.get(getAppPath(), "config", "config.properties").toString();
        }

        private String getAppPath() {
          Path pluginPath = Paths.get("survival2d-app-entry");
          if (!Files.exists(pluginPath)) {
            pluginPath = Paths.get("../survival2d-app-entry");
          }
          return pluginPath.toString();
        }
      };
    }
  }
}
