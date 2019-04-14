package cn.edu.cqvie.iocp.server;

import cn.edu.cqvie.iocp.engine.constant.SystemConstant;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 项目启动类
 *
 * @author ZHENG SHAOHONG
 */
public class MessageServer {

    private static final Logger logger = LoggerFactory.getLogger(MessageServer.class);

    private static EventLoopGroup bossGroup;
    private static EventLoopGroup workerGroup;
    private static ChannelFuture channelFuture;

    public static void main(String[] args) throws InterruptedException {
        start();
    }


    public static void start(int port) throws InterruptedException {
        int nThread = Runtime.getRuntime().availableProcessors() * 2;
        bossGroup = new NioEventLoopGroup(nThread);
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new MessageChannelInitializer())
                    .option(ChannelOption.SO_BACKLOG, 4096)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            channelFuture = b.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            stop();
        }
    }

    public static void start() throws InterruptedException {
        start(SystemConstant.SERVER_PORT);
    }

    public static boolean isOpen() {
        if (null != channelFuture && channelFuture.channel() != null) {
            return channelFuture.channel().isOpen();
        } else {
            return false;
        }
    }

    public static void stop() {
        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }
}
