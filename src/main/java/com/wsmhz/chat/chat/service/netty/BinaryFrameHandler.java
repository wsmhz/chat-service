package com.wsmhz.chat.chat.service.netty;

import com.google.common.collect.Lists;
import com.wsmhz.chat.chat.service.api.domain.form.ChatMsgForm;
import com.wsmhz.chat.chat.service.service.ChatMsgService;
import com.wsmhz.chat.chat.service.service.impl.ChatMsgServiceImpl;
import com.wsmhz.chat.chat.service.utils.NettyUtil;
import com.wsmhz.common.business.properties.BusinessProperties;
import com.wsmhz.common.business.utils.AssertUtil;
import com.wsmhz.common.business.utils.FTPUtil;
import com.wsmhz.common.business.utils.JsonUtil;
import com.wsmhz.common.business.utils.SpringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Created By tangbj On 2019/6/15
 * Description: 处理二进制消息的handler
 */
@Slf4j
public class BinaryFrameHandler extends SimpleChannelInboundHandler<BinaryWebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BinaryWebSocketFrame msg) throws Exception {
        log.info(" ****** 接收到二进制消息 *****");
        ByteBuf content = msg.content();
        content.markReaderIndex();

        // **************  获取二进制中的文本内容部分 ***************

        // 获取文本长度
        int extraMsgLength = content.readInt();
        AssertUtil.requireNonNull(extraMsgLength, "消息文本长度不能为空");
        // 获取文本内容
        String extraMsg = content.readCharSequence(extraMsgLength, StandardCharsets.UTF_8).toString();
        AssertUtil.requireNotBlank(extraMsg, "消息文本不能为空");
        log.info("携带消息文本长度：{}，内容：{}",extraMsgLength, extraMsg);
        ChatMsgForm chatMsgForm = JsonUtil.stringToObj(extraMsg, ChatMsgForm.class);
        ChatMsgForm.MsgContext msgContext = chatMsgForm.getMsgContext();
        AssertUtil.requireNonNull(msgContext,"消息内容部分不能为空");

        // *************** 获取二进制中的文件内容部分 *****************

        ByteBuf byteBuf = Unpooled.directBuffer(msg.content().capacity());
        byteBuf.writeBytes(msg.content());
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        // 上传到本地缓存后，接着上传到ftp，最后删除本地缓存
        String fileUploadName = UUID.randomUUID().toString() + "." + msgContext.getContext();
        log.info("上传文件新文件名为：{}", fileUploadName);
        File targetFile = new File(fileUploadName);
        FileUtils.writeByteArrayToFile(targetFile, bytes);
        FTPUtil.uploadFile(Lists.newArrayList(targetFile));
        log.info("删除本地缓存上传的文件结果为：{}", targetFile.delete());

        // 获取到上传后的FTP文件url
        BusinessProperties businessProperties = SpringUtil.getBean(BusinessProperties.class);
        String fileUrl = businessProperties.getFtp().getHttpPrefix() + targetFile.getName();
        // 重新设置本条消息的内容为该url
        msgContext.setContext(fileUrl);
        // 保存消息，并标记为未签收
        ChatMsgService chatMsgService = SpringUtil.getBean(ChatMsgServiceImpl.class);
        msgContext.setMsgId(UUID.randomUUID().toString());
        // 异常，需要通过消息补偿机制等其他方式进行处理
        AssertUtil.requireTrue(chatMsgService.saveMsg(msgContext) > 0, "保存消息到数据库异常");
        // 发送消息给接收者
        Channel receiverChannel = NettyUtil.validateChannel(msgContext.getReceiverId());
        if(receiverChannel != null){
            receiverChannel.writeAndFlush(
                    new TextWebSocketFrame(
                            JsonUtil.objToString(chatMsgForm)));
        }
    }
}
