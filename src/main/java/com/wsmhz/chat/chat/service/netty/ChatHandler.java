package com.wsmhz.chat.chat.service.netty;

import com.wsmhz.chat.chat.service.api.domain.form.ChatMsgForm;
import com.wsmhz.chat.chat.service.enums.MsgActionEnum;
import com.wsmhz.chat.chat.service.service.ChatMsgService;
import com.wsmhz.chat.chat.service.service.impl.ChatMsgServiceImpl;
import com.wsmhz.common.business.utils.AssertUtil;
import com.wsmhz.common.business.utils.JsonUtil;
import com.wsmhz.common.business.utils.SpringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.UUID;

/**
 * Created By tangbj On 2019/6/12
 * Description:  处理消息的handler
 * TextWebSocketFrame： 在netty中，是用于为websocket专门处理文本的对象，frame是消息的载体
 */
@Slf4j
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    // 用于记录和管理所有客户端的channle
    public static ChannelGroup users = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        String content = msg.text();
        System.out.println(content);
//        Channel currentChannel = ctx.channel();
//        ChatMsgForm chatMsgForm = JsonUtil.stringToObj(content, ChatMsgForm.class);
//        AssertUtil.requireNonNull(chatMsgForm.getAction(), "action动作不能为空");
//        MsgActionEnum msgActionEnum = MsgActionEnum.valueOf(chatMsgForm.getAction());
//        // 判断消息类型，根据不同的类型来处理不同的业务
//        ChatMsgService chatMsgService = SpringUtil.getBean(ChatMsgServiceImpl.class);
//        switch (msgActionEnum){
//            case CONNECT:
//                // 第一次open的时候，初始化channel，把用的channel和userId关联起来
//                String senderId = chatMsgForm.getMsgContext().getSenderId();
//                UserChannelRel.put(senderId, currentChannel);
//                users.forEach(users ->{
//                    log.info(users.id().asLongText());
//                });
//                UserChannelRel.output();
//            break;
//            case CHAT:
//                // 保存消息到数据库，并且标记为 未签收
//                ChatMsgForm.MsgContext msgContext = chatMsgForm.getMsgContext();
//                msgContext.setMsgId(UUID.randomUUID().toString());
//                AssertUtil.requireTrue(chatMsgService.saveMsg(msgContext) > 0, "保存消息到数据库异常");
//                // 发送消息给接收者，从全局用户Channel关系中获取接受方的channel
//                Channel receiverChannel = UserChannelRel.get(msgContext.getReceiverId());
//                if (receiverChannel == null) {
//                    // TODO channel为空代表用户离线，推送消息
//                } else {
//                    Channel findChannel = users.find(receiverChannel.id());
//                    if (findChannel != null) {
//                        receiverChannel.writeAndFlush(
//                                new TextWebSocketFrame(
//                                        JsonUtil.objToString(chatMsgForm)));
//                    } else {
//                        // TODO 推送消息
//                    }
//                }
//            break;
//            case SIGNED:
//                // 针对具体的消息进行签收
//                List<String> signMsgIds = chatMsgForm.getSignMsgIds();
//                if (CollectionUtils.isNotEmpty(signMsgIds)) {
//                    log.info("需要签收的消息为：{}", signMsgIds);
//
//                    chatMsgService.signMsg(signMsgIds);
//                }
//            break;
//            case KEEP_ALIVE:
//                // 心跳类型的消息
//                log.info("收到来自channel为{}的心跳包...", currentChannel);
//        }
    }

    /**
     * 当客户端连接服务端之后（打开连接） 放到ChannelGroup中去进行管理
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx){
        users.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx){
        log.info("客户端被移除，channelId为：{}", ctx.channel().id().asLongText());
        // 移除对应客户端的channel
        users.remove(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        // 发生异常之后关闭连接（关闭channel），随后从ChannelGroup中移除
        ctx.channel().close();
        users.remove(ctx.channel());
    }


}
