package survival2d.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NetworkStartup {
  public static void setup() {
    InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE);
    var parentGroup = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
    var childGroup = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();

    var serverBootstrap = new ServerBootstrap();
    serverBootstrap
        .group(parentGroup, childGroup)
        .channel(NioServerSocketChannel.class)
        .handler(new LoggingHandler(LogLevel.DEBUG))
        .childHandler(
            new ChannelInitializer<>() {

              @Override
              protected void initChannel(Channel channel) {
                channel
                    .pipeline()
                    .addLast(new LoggingHandler(LogLevel.DEBUG))
                    .addLast(new HttpServerCodec())
                    .addLast(new HttpObjectAggregator(65536))
                    .addLast(new WebSocketServerCompressionHandler())
                    .addLast(new WebSocketServerProtocolHandler("/fbs", null, true))
                    .addLast(new FlatBuffersDecoder())
                    .addLast(new WebsocketHandler());
              }
            })
        .childHandler(
            new ChannelInitializer<>() {

              @Override
              protected void initChannel(Channel channel) {
                channel
                    .pipeline()
                    .addLast(new LoggingHandler(LogLevel.DEBUG))
                    .addLast(new HttpServerCodec())
                    .addLast(new HttpObjectAggregator(65536))
                    .addLast(new WebSocketServerCompressionHandler())
                    .addLast(new WebSocketServerProtocolHandler("/json", null, true))
                    .addLast(new WebsocketHandler());
              }
            });

    try {
      var channelFuture = serverBootstrap.bind(1202).sync();
      channelFuture.channel().closeFuture().sync();
    } catch (InterruptedException interruptedException) {
      log.error("Interrupted", interruptedException);
    } finally {
      parentGroup.shutdownGracefully();
      childGroup.shutdownGracefully();
    }
  }
}
