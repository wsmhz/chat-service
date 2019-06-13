package com.wsmhz.chat.chat.service.service.impl;

import com.wsmhz.chat.chat.service.api.domain.form.ChatMsgForm;
import com.wsmhz.chat.chat.service.api.domain.vo.UnSignMsgVo;
import com.wsmhz.chat.chat.service.domain.entity.ChatMsg;
import com.wsmhz.chat.chat.service.enums.MsgSignEnum;
import com.wsmhz.chat.chat.service.mapper.ChatMsgMapper;
import com.wsmhz.chat.chat.service.service.ChatMsgService;
import com.wsmhz.common.business.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created By tangbj On 2019/6/12
 * Description:
 */
@Service
public class ChatMsgServiceImpl extends BaseServiceImpl<ChatMsg> implements ChatMsgService {

    @Autowired
    private ChatMsgMapper chatMsgMapper;

    @Override
    public int saveMsg(ChatMsgForm.MsgContext msgContext) {
        Date date = new Date();
        ChatMsg chatMsg = ChatMsg.builder()
                            .msgId(msgContext.getMsgId())
                            .senderId(msgContext.getSenderId())
                            .receiverId(msgContext.getReceiverId())
                            .context(msgContext.getContext())
                            .msgType(msgContext.getMsgType())
                            .signFlag(MsgSignEnum.UN_SIGN.getId())
                            .createDate(date)
                            .updateDate(date)
                            .build();
        return chatMsgMapper.insert(chatMsg);
    }

    @Transactional
    @Override
    public int signMsg(List<String> msgIds) {
        msgIds.forEach(id -> {
            Example example = new Example(ChatMsg.class);
            example.createCriteria().andEqualTo("msgId", id);
            ChatMsg chatMsg = ChatMsg.builder().signFlag(MsgSignEnum.SIGNED.getId()).build();
            chatMsgMapper.updateByExampleSelective(chatMsg, example);
        });
        return 1;
    }

    @Override
    public UnSignMsgVo findUnSignMsgList(String receiverId) {
        Example example = new Example(ChatMsg.class);
        example.createCriteria()
                .andEqualTo("receiverId", receiverId)
                .andEqualTo("signFlag", MsgSignEnum.UN_SIGN.getId());
        List<ChatMsg> chatMsgs = chatMsgMapper.selectByExample(example);
        UnSignMsgVo unSignMsgVo = new UnSignMsgVo();
        List<ChatMsgForm.MsgContext> msgContextList = new ArrayList<>();
        List<String> signMsgIdList = new ArrayList<>();
        chatMsgs.forEach(chatMsg -> {
            msgContextList.add(
                ChatMsgForm.MsgContext.builder()
                        .senderId(chatMsg.getSenderId())
                        .receiverId(chatMsg.getReceiverId())
                        .msgId(chatMsg.getMsgId())
                        .context(chatMsg.getContext())
                        .msgType(chatMsg.getMsgType())
                        .build()
            );
            signMsgIdList.add(chatMsg.getMsgId());
        });
        unSignMsgVo.setMsgContexts(msgContextList);
        unSignMsgVo.setSignMsgIds(signMsgIdList);
        return unSignMsgVo;
    }
}
