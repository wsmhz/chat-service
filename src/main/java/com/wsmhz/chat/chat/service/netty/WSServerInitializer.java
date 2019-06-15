package com.wsmhz.chat.chat.service.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Created By tangbj On 2019/6/12
 * Description: 初始化配置
 */
public class WSServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        // ************ http协议 ************

        // http编解码器
        pipeline.addLast(new HttpServerCodec());
        // 大数据流
        pipeline.addLast(new ChunkedWriteHandler());
        // httpMessage消息转换
        // 需要放到HttpServerCodec这个处理器后面
        pipeline.addLast(new HttpObjectAggregator(1024 * 64 * 2));

        // ************* 增加心跳支持 ****************

        // 长时间没有向服务端发送心跳，则主动断开
        pipeline.addLast(new IdleStateHandler(30, 45, 60));
        pipeline.addLast(new HeartBeatHandler());

        // ************** 用于支持httpWebsocket ***************

        // 数据压缩扩展，当添加这个的时候WebSocketServerProtocolHandler的第三个参数需要设置成true
        pipeline.addLast(new WebSocketServerCompressionHandler());
        //  指定给客户端连接访问的路由 : /ws
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws", null, true, 1024 * 64 * 2));
        // 处理文本信息
        pipeline.addLast(new TextFrameHandler());
        // 处理二进制消息
        pipeline.addLast(new BinaryFrameHandler());
    }



}
