package com.survival2d.server;

import com.tvd12.ezyfox.bean.EzyBeanContextBuilder;
import com.tvd12.ezyfox.codec.JacksonCodecCreator;
import com.tvd12.ezyfox.codec.MsgPackCodecCreator;
import com.tvd12.ezyfoxserver.constant.EzyEventType;
import com.tvd12.ezyfoxserver.constant.EzyMaxRequestPerSecondAction;
import com.tvd12.ezyfoxserver.context.EzyAppContext;
import com.tvd12.ezyfoxserver.context.EzyPluginContext;
import com.tvd12.ezyfoxserver.context.EzyServerContext;
import com.tvd12.ezyfoxserver.embedded.EzyEmbeddedServer;
import com.tvd12.ezyfoxserver.ext.EzyAbstractAppEntryLoader;
import com.tvd12.ezyfoxserver.ext.EzyAbstractPluginEntryLoader;
import com.tvd12.ezyfoxserver.ext.EzyAppEntry;
import com.tvd12.ezyfoxserver.ext.EzyPluginEntry;
import com.tvd12.ezyfoxserver.setting.EzyAppSettingBuilder;
import com.tvd12.ezyfoxserver.setting.EzyPluginSettingBuilder;
import com.tvd12.ezyfoxserver.setting.EzySessionManagementSettingBuilder;
import com.tvd12.ezyfoxserver.setting.EzySessionManagementSettingBuilder.EzyMaxRequestPerSecondBuilder;
import com.tvd12.ezyfoxserver.setting.EzySettingsBuilder;
import com.tvd12.ezyfoxserver.setting.EzySimpleAppSetting;
import com.tvd12.ezyfoxserver.setting.EzySimplePluginSetting;
import com.tvd12.ezyfoxserver.setting.EzySimpleSessionManagementSetting;
import com.tvd12.ezyfoxserver.setting.EzySimpleSessionManagementSetting.EzySimpleMaxRequestPerSecond;
import com.tvd12.ezyfoxserver.setting.EzySimpleSettings;
import com.tvd12.ezyfoxserver.setting.EzySimpleSocketSetting;
import com.tvd12.ezyfoxserver.setting.EzySimpleUdpSetting;
import com.tvd12.ezyfoxserver.setting.EzySimpleUserManagementSetting;
import com.tvd12.ezyfoxserver.setting.EzySimpleWebSocketSetting;
import com.tvd12.ezyfoxserver.setting.EzySimpleZoneSetting;
import com.tvd12.ezyfoxserver.setting.EzySocketSettingBuilder;
import com.tvd12.ezyfoxserver.setting.EzyUdpSettingBuilder;
import com.tvd12.ezyfoxserver.setting.EzyUserManagementSettingBuilder;
import com.tvd12.ezyfoxserver.setting.EzyWebSocketSettingBuilder;
import com.tvd12.ezyfoxserver.setting.EzyZoneSettingBuilder;
import com.tvd12.ezyfoxserver.support.entry.EzySimpleAppEntry;
import com.tvd12.ezyfoxserver.support.entry.EzySimplePluginEntry;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerStartup {

  public static final String ZONE_NAME = "survival2d";
  public static final String APP_NAME = "survival2d";
  public static final String PLUGIN_NAME = "survival2d";
  public static final String NODE_NAME = "survival2d";
  @Getter private static EzyServerContext serverContext;

  public static void main(String[] args) throws Exception {
    log.trace("Start config server");
    EzySimpleSocketSetting socketSetting =
        new EzySocketSettingBuilder()
            .active(true)
            .address("0.0.0.0")
            .codecCreator(MsgPackCodecCreator.class)
            .maxRequestSize(1024)
            .port(3005)
            .tcpNoDelay(true)
            .writerThreadPoolSize(8)
            .build();
    EzySimpleWebSocketSetting webSocketSetting =
        new EzyWebSocketSettingBuilder()
            .active(true)
            .address("0.0.0.0")
            .codecCreator(JacksonCodecCreator.class)
            .maxFrameSize(1024)
            .port(2208)
            .writerThreadPoolSize(8)
            .build();
    EzySimplePluginSetting pluginSetting =
        new EzyPluginSettingBuilder()
            .name(PLUGIN_NAME)
            .addListenEvent(EzyEventType.USER_LOGIN)
            //        .configFile("config.properties")
            .entryLoader(Survival2dPluginEntryLoader.class)
            //      .entryLoaderArgs()
            .priority(1)
            .threadPoolSize(3)
            .build();

    EzySimpleAppSetting appSetting =
        new EzyAppSettingBuilder()
            .name(APP_NAME)
            //        .configFile("config.properties")
            .entryLoader(Survival2dAppEntryLoader.class)
            .maxUsers(9999)
            //      .entryLoaderArgs()
            .threadPoolSize(3)
            .build();

    EzySimpleUserManagementSetting userManagementSetting =
        new EzyUserManagementSettingBuilder()
            .allowChangeSession(true)
            .allowGuestLogin(true)
            .guestNamePrefix("Guest#")
            .maxSessionPerUser(5)
            .userMaxIdleTimeInSecond(15)
            .userNamePattern("^[a-z0-9_.]{3,36}$")
            .build();

//    EzySimpleStreamingSetting streamingSetting = new EzySimpleStreamingSetting();
//    streamingSetting.setEnable(true);

//    EzySimpleStreamingSetting streamingSetting2 = new EzySimpleStreamingSetting();
//    streamingSetting2.setEnable(false);

    EzySimpleZoneSetting zoneSetting =
        new EzyZoneSettingBuilder()
            .name(ZONE_NAME)
            .plugin(pluginSetting)
            .application(appSetting)
            //        .configFile("config.properties")
            .maxUsers(999999)
            .userManagement(userManagementSetting)
            // add event controller, accept SERVER_INITIALIZING, SERVER_READY
//            .addEventController(EzyEventType.STREAMING, StreamingController.class)
//            .streaming(streamingSetting)
            .build();

    EzySimpleMaxRequestPerSecond maxRequestPerSecond =
        new EzyMaxRequestPerSecondBuilder()
            .value(250)
            .action(EzyMaxRequestPerSecondAction.DROP_REQUEST)
            .build();

    EzySimpleSessionManagementSetting sessionManagementSetting =
        new EzySessionManagementSettingBuilder()
            .sessionMaxIdleTimeInSecond(30)
            .sessionMaxWaitingTimeInSecond(30)
            .sessionMaxRequestPerSecond(maxRequestPerSecond)
            .build();

    EzySimpleUdpSetting udpSetting =
        new EzyUdpSettingBuilder()
            .active(true)
            .address("0.0.0.0")
            .channelPoolSize(16)
            .codecCreator(MsgPackCodecCreator.class)
            .handlerThreadPoolSize(5)
            .maxRequestSize(1024)
            .port(2611)
            .build();

    EzySimpleSettings settings =
        new EzySettingsBuilder()
            .debug(true)
            .nodeName(NODE_NAME)
            .zone(zoneSetting)
            .socket(socketSetting)
            .websocket(webSocketSetting)
            .udp(udpSetting)
            .sessionManagement(sessionManagementSetting)
//            .addEventController(EzyEventType.STREAMING, StreamingController.class)
//            .streaming(streamingSetting2)
            .build();

    EzyEmbeddedServer server = EzyEmbeddedServer.builder().settings(settings).build();

    log.trace("Config complete! Start server...");
    serverContext = server.start();
    log.trace("Start server complete!");
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
      return new String[] {"com.survival2d.server"};
    }

    @Override
    protected String[] getScanableBindingPackages() {
      return new String[] {"com.survival2d.server"};
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
