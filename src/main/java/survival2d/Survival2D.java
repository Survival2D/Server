package survival2d;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import survival2d.network.FlatBuffersDecoder;
import survival2d.network.WebsocketHandler;

@Slf4j
public class Survival2D {

  public static void main(String[] args) {
    log.trace("Begin init server");
    setupCommon();
    setupFbs();
    setupJson();
    log.trace("Complete init server!");
  }

  private static void setupCommon() {
    InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE);
  }

  private static void setupFbs() {
    var parentGroup = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
    var childGroup = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
    var channelClass =
        Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class;

    var serverBootstrap = new ServerBootstrap();
    serverBootstrap
        .group(parentGroup, childGroup)
        .channel(channelClass)
        .localAddress(new InetSocketAddress(ServerConstant.FBS_PORT))
        .handler(new LoggingHandler(LogLevel.DEBUG))
        .childHandler(
            new ChannelInitializer<>() {

              @Override
              protected void initChannel(Channel channel) {
                channel
                    .pipeline()
                    .addLast(new IdleStateHandler(60 * 30, 0, 0, TimeUnit.SECONDS))
                    .addLast(new HttpServerCodec())
                    .addLast(new ChunkedWriteHandler())
                    .addLast(new HttpObjectAggregator(65536))
                    .addLast(new WebSocketServerCompressionHandler())
                    .addLast(new WebSocketServerProtocolHandler("/fbs", null, true))
                    .addLast(new FlatBuffersDecoder())
                    .addLast(new WebsocketHandler());
              }
            });

    try {
      var channelFuture = serverBootstrap.bind().sync();
      channelFuture.channel().closeFuture().sync();
    } catch (InterruptedException interruptedException) {
      log.error("Interrupted", interruptedException);
    } finally {
      parentGroup.shutdownGracefully();
      childGroup.shutdownGracefully();
    }
  }

  public static void setupJson() {
    var parentGroup = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
    var childGroup = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
    var channelClass =
        Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class;

    var serverBootstrap = new ServerBootstrap();
    serverBootstrap
        .group(parentGroup, childGroup)
        .channel(channelClass)
        .localAddress(new InetSocketAddress(ServerConstant.FBS_PORT))
        .handler(new LoggingHandler(LogLevel.DEBUG))
        .childHandler(
            new ChannelInitializer<>() {

              @Override
              protected void initChannel(Channel channel) {
                channel
                    .pipeline()
                    .addLast(new IdleStateHandler(60 * 30, 0, 0, TimeUnit.SECONDS))
                    .addLast(new HttpServerCodec())
                    .addLast(new ChunkedWriteHandler())
                    .addLast(new HttpObjectAggregator(65536))
                    .addLast(new WebSocketServerCompressionHandler())
                    .addLast(new WebSocketServerProtocolHandler("/json", null, true))
                    .addLast(new WebsocketHandler());
              }
            });

    try {
      var channelFuture = serverBootstrap.bind().sync();
      channelFuture.channel().closeFuture().sync();
    } catch (InterruptedException interruptedException) {
      log.error("Interrupted", interruptedException);
    } finally {
      parentGroup.shutdownGracefully();
      childGroup.shutdownGracefully();
    }
  }
}
