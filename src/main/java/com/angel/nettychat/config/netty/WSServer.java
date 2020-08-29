package com.angel.nettychat.config.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;

/**
 * @Author angel
 * @Date 19-10-30
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class WSServer {
    /**
     * 定义一对线程组（两个线程池）
     *
     */
    //主线程组，用于接收客户端的链接，但不做任何处理
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    //定义从线程组，主线程组会把任务转给从线程组进行处理
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

//    @Autowired
    private final WSServerInitialzer wsServerInitialzer;

    @Value("${netty.port}")
    private int port;

    /**
     * 启动Netty Server
     *
     * @throws InterruptedException
     */
    @PostConstruct
    public void start() throws InterruptedException {
        /**
         * 服务启动类，任务分配自动处理
         *
         */
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        //需要去针对一个之前的线程模型（上面定义的是主从线程）
        serverBootstrap.group(bossGroup, workerGroup)
                //设置NIO的双向通道
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(port))
                //子处理器，用于处理workerGroup
                /**
                 * 设置chanel初始化器
                 * 每一个chanel由多个handler共同组成管道(pipeline)
                 */
                .childHandler(wsServerInitialzer);

        /**
         * 启动
         *
         */
        //绑定端口，并设置为同步方式，是一个异步的chanel
        ChannelFuture future = serverBootstrap.bind().sync();

        if (future.isSuccess()) {
            log.info("启动 Netty Server");
        }
    }

    @PreDestroy
    public void destroy() throws InterruptedException {
        bossGroup.shutdownGracefully().sync();
        workerGroup.shutdownGracefully().sync();
        log.info("关闭Netty");
    }

}
