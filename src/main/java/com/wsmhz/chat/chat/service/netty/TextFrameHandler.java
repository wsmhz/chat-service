package com.wsmhz.chat.chat.service.netty;

import com.wsmhz.chat.chat.service.api.domain.form.ChatMsgForm;
import com.wsmhz.chat.chat.service.api.enums.MsgActionEnum;
import com.wsmhz.chat.chat.service.service.ChatMsgService;
import com.wsmhz.chat.chat.service.service.impl.ChatMsgServiceImpl;
import com.wsmhz.chat.chat.service.utils.NettyUtil;
import com.wsmhz.common.business.utils.AssertUtil;
import com.wsmhz.common.business.utils.JsonUtil;
import com.wsmhz.common.business.utils.SpringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.UUID;

/**
 * Created By tangbj On 2019/6/12
 * Description:  处理文本消息的handler
 */
@Slf4j
public class  TextFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @SneakyThrows
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        String content = msg.text();
        log.info("收到的文本消息内容为：{}", content);
        Channel currentChannel = ctx.channel();
        ChatMsgForm chatMsgForm = JsonUtil.stringToObj(content, ChatMsgForm.class);
        AssertUtil.requireNonNull(chatMsgForm.getAction(), "action动作不能为空");
        MsgActionEnum msgActionEnum = MsgActionEnum.valueOf(chatMsgForm.getAction());
        ChatMsgService chatMsgService = SpringUtil.getBean(ChatMsgServiceImpl.class);
        // 判断动作类型
        switch (msgActionEnum){
            case CONNECT:
                // 初始化连接，把channel和userId进行关联
                String senderId = chatMsgForm.getMsgContext().getSenderId();
                NettyUtil.putRelation(senderId, currentChannel);
                NettyUtil.printRelation();
                break;
            case CHAT:
                AssertUtil.requireNonNull(chatMsgForm.getMsgContext(),"消息内容部分不能为空");
                // 保存消息，并标记为未签收
                ChatMsgForm.MsgContext msgContext = chatMsgForm.getMsgContext();
                msgContext.setMsgId(UUID.randomUUID().toString());
                // 异常，需要通过消息补偿机制等其他方式进行处理
                AssertUtil.requireTrue(chatMsgService.saveMsg(msgContext) > 0, "保存消息到数据库异常");
                // 发送给接收者
                Channel receiverChannel = NettyUtil.validateChannel(msgContext.getReceiverId());
                if(receiverChannel != null){
                    receiverChannel.writeAndFlush(
                            new TextWebSocketFrame(
                                    JsonUtil.objToString(chatMsgForm)));
                }
                break;
            case SIGN:
                // 签收
                List<String> signMsgIds = chatMsgForm.getSignMsgIds();
                if (CollectionUtils.isNotEmpty(signMsgIds)) {
                    log.info("需要签收的消息为：{}", signMsgIds);
                    chatMsgService.signMsg(signMsgIds);
                }
                break;
            case KEEP_ALIVE:
                // 心跳
                log.info("收到来自channel为{}的心跳包...", currentChannel);
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx){
        NettyUtil.addUser(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx){
        log.info("客户端被移除，channelId为：{}", ctx.channel().id().asLongText());
        NettyUtil.removeUser(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        log.info("客户端异常关闭，channelId为：{}", ctx.channel().id().asLongText(), cause);
        ctx.channel().close();
        NettyUtil.removeUser(ctx.channel());
    }
}
