package com.wsmhz.chat.chat.service.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

/**
 * Created By tangbj On 2019/6/12
 * Description: 初始化配置
 */
public class WSServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        // ====================== 用于支持http协议    ======================

        // websocket 基于http协议，http编解码器

        pipeline.addLast(new HttpServerCodec());
//        // 对写大数据流的支持
        pipeline.addLast(new ChunkedWriteHandler());
//        // 对httpMessage进行聚合，聚合成FullHttpRequest或FullHttpResponse
        pipeline.addLast(new HttpObjectAggregator(1024 * 64 * 5));


        // ====================== 增加心跳支持  ======================

        // 针对客户端，如果在1分钟时没有向服务端发送读写心跳(ALL)，则主动断开
        // 如果是读空闲或者写空闲，不处理
        pipeline.addLast(new IdleStateHandler(30, 45, 60));
        // 自定义的空闲状态检测
        pipeline.addLast(new HeartBeatHandler());

        // ====================== 用于支持httpWebsocket ======================

        //  websocket 服务器处理的协议，用于指定给客户端连接访问的路由 : /ws
         // 处理握手动作 以frames进行传输的，不同的数据类型对应的frames也不同
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        // 自定义的handler
        pipeline.addLast(new ChatHandler());
    }



}
