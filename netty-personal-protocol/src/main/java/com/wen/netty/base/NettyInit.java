package com.wen.netty.base;

import com.wen.entity.NettyYamlConfig;
import com.wen.netty.service.NettyPersonalProtocolServiceHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author: 7wen
 * @Date: 2023-04-21 10:58
 * @description:
 */
public class NettyInit {

    public NettyInit(NettyYamlConfig.TcpConfig tcpConfig) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(tcpConfig.getBossThreadSize());
        EventLoopGroup workGroup = new NioEventLoopGroup(tcpConfig.getWorkThreadSize());

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            channel.pipeline().addLast(new NettyPersonalProtocolDecode());
                            channel.pipeline().addLast(new NettyPersonalProtocolServiceHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = serverBootstrap.bind(tcpConfig.getTcpPort()).sync();

            f.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
