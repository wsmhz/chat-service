package com.wsmhz.chat.chat.service.netty;

import com.wsmhz.chat.chat.service.properties.NettyProperties;
import com.wsmhz.common.business.utils.SpringUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created By tangbj On 2019/6/12
 * Description: 服务端配置
 */
@Slf4j
@Component
public class WSServer {

    private static class SingleWSServer {
        static final WSServer instance = new WSServer();
    }

    public static WSServer getInstance() {
        return SingleWSServer.instance;
    }

    private ServerBootstrap server;

    public WSServer() {
        EventLoopGroup mainGroup = new NioEventLoopGroup();
        EventLoopGroup subGroup = new NioEventLoopGroup();
        server = new ServerBootstrap();
        server.group(mainGroup, subGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new WSServerInitializer());
    }

    public void start() {
        NettyProperties nettyProperties = SpringUtil.getBean("NettyProperties",NettyProperties.class);
        server.bind(nettyProperties.getPort());
        log.info("*********** netty websocket server 启动完毕 *************");
    }
}
