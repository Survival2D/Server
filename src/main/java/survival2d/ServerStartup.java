package survival2d;

import com.tvd12.ezyfox.bean.EzyBeanContextBuilder;
import com.tvd12.ezyfoxserver.constant.EzyEventType;
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
import com.tvd12.ezyfoxserver.setting.EzySimpleStreamingSetting;
import com.tvd12.ezyfoxserver.setting.EzySimpleUserManagementSetting;
import com.tvd12.ezyfoxserver.setting.EzySimpleWebSocketSetting;
import com.tvd12.ezyfoxserver.setting.EzySimpleZoneSetting;
import com.tvd12.ezyfoxserver.setting.EzyUserManagementSettingBuilder;
import com.tvd12.ezyfoxserver.setting.EzyWebSocketSettingBuilder;
import com.tvd12.ezyfoxserver.setting.EzyZoneSettingBuilder;
import com.tvd12.ezyfoxserver.support.entry.EzySimpleAppEntry;
import com.tvd12.ezyfoxserver.support.entry.EzySimplePluginEntry;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import survival2d.network.StreamingController;

@Slf4j
public class ServerStartup {

  public static final String ZONE_NAME = "survival2d";
  public static final String APP_NAME = "survival2d";
  public static final String PLUGIN_NAME = "survival2d";
  public static final String NODE_NAME = "survival2d";
  public static final String PACKAGE_NAME =
      "survival2d"; // ServerStartup.class.getPackage().getName();
  @Getter private static EzyServerContext serverContext;

  public static void main(String[] args) throws Exception {
    log.trace("Start config server");
    //    EzySimpleSocketSetting socketSetting = new EzySocketSettingBuilder()
    //        .active(true) // active or not,  default true
    //        .address("0.0.0.0") // loopback address, default 0.0.0.0
    //        .codecCreator(MsgPackCodecCreator.class) // encoder/decoder creator, default
    // MsgPackCodecCreator
    //        .maxRequestSize(1024) // max request size, default 32768
    //        .port(3005) // port, default 3005
    //        .tcpNoDelay(true) // tcp no delay, default false
    //        .writerThreadPoolSize(8) // thread pool size for socket writer, default 8
    //        .build();
    EzySimpleWebSocketSetting webSocketSetting =
        new EzyWebSocketSettingBuilder()
            //            .codecCreator(Survival2DCodecCreator.class)
            .build();
    EzySimplePluginSetting pluginSetting =
        new EzyPluginSettingBuilder()
            .name(PLUGIN_NAME)
            .entryLoader(Survival2dPluginEntryLoader.class)
            .build();
    EzySimpleAppSetting appSetting =
        new EzyAppSettingBuilder()
            .name(APP_NAME)
            .entryLoader(Survival2dAppEntryLoader.class)
            .build();
    EzySimpleUserManagementSetting userManagementSetting =
        new EzyUserManagementSettingBuilder()
            .allowGuestLogin(true)
            .userMaxIdleTimeInSecond(15)
            .build();
    EzySimpleStreamingSetting streamingSetting = new EzySimpleStreamingSetting();
    streamingSetting.setEnable(true);
    EzySimpleZoneSetting zoneSetting =
        new EzyZoneSettingBuilder()
            .name(ZONE_NAME)
            .plugin(pluginSetting)
            .application(appSetting)
            .userManagement(userManagementSetting)
            .streaming(streamingSetting)
            .addEventController(EzyEventType.STREAMING, StreamingController.class)
            .build();
    EzySimpleMaxRequestPerSecond maxRequestPerSecond =
        new EzyMaxRequestPerSecondBuilder()
            .value(300) // 60 tick mỗi giây, có thể có nhiều action trong 1 tick
            .build();
    EzySimpleSessionManagementSetting sessionManagementSetting =
        new EzySessionManagementSettingBuilder()
            .sessionMaxRequestPerSecond(maxRequestPerSecond)
            .build();

    //    EzySimpleUdpSetting udpSetting =
    //        new EzyUdpSettingBuilder()
    //            .active(true)
    //            .address("0.0.0.0")
    //            .channelPoolSize(16)
    //            .codecCreator(MsgPackCodecCreator.class)
    //            .handlerThreadPoolSize(5)
    //            .maxRequestSize(1024)
    //            .port(2611)
    //            .build();

    EzySimpleSettings settings =
        new EzySettingsBuilder()
            .debug(true)
            .nodeName(NODE_NAME)
            .zone(zoneSetting)
            .websocket(webSocketSetting)
            .sessionManagement(sessionManagementSetting)
            .streaming(streamingSetting)
            //            .socket(socketSetting)
            //            .addEventController(EzyEventType.STREAMING, StreamingController.class)
            .build();

    EzyEmbeddedServer server = EzyEmbeddedServer.builder().settings(settings).build();

    log.trace("Config complete! Start server...");
    serverContext = server.start();
    log.trace("Start server complete!");
  }

  public static class Survival2dAppEntry extends EzySimpleAppEntry {

    @Override
    protected String[] getScanableBeanPackages() {
      return new String[] {PACKAGE_NAME};
    }

    @Override
    protected String[] getScanableBindingPackages() {
      return new String[] {PACKAGE_NAME};
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
      return new String[] {PACKAGE_NAME};
    }

    @Override
    protected String[] getScanableBindingPackages() {
      return new String[] {PACKAGE_NAME};
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
