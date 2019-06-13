package com.wsmhz.chat.chat.service.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * Created By tangbj On 2019/6/12
 * Description: 用于检测channel的心跳handler
 */
@Slf4j
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt){

        // 判断evt是否是IdleStateEvent（用于触发用户事件，包含 读空闲/写空闲/读写空闲 ）
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state() == IdleState.READER_IDLE) {
                log.info("channelLongId：{}进入读空闲...", ctx.channel().id().asLongText());
            } else if (event.state() == IdleState.WRITER_IDLE) {
                log.info("channelLongId：{}进入写空闲...", ctx.channel().id().asLongText());
            } else if (event.state() == IdleState.ALL_IDLE) {
                log.info("channelLongId：{}关闭前，users的数量为：{}", ctx.channel().id().asLongText(), ChatHandler.users.size());
                // 关闭无用的channel
                ctx.channel().close();
                log.info("channelLongId：{}关闭后，users的数量为：{}", ctx.channel().id().asLongText(), ChatHandler.users.size());
            }
        }

    }

}
