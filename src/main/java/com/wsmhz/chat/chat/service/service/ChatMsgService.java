package com.wsmhz.chat.chat.service.service;

import com.wsmhz.chat.chat.service.api.domain.form.ChatMsgForm;
import com.wsmhz.chat.chat.service.api.domain.vo.UnSignMsgVo;
import com.wsmhz.chat.chat.service.domain.entity.ChatMsg;
import com.wsmhz.common.business.service.BaseService;

import java.util.List;

/**
 * Created By tangbj On 2019/6/12
 * Description:
 */
public interface ChatMsgService extends BaseService<ChatMsg> {

    int saveMsg(ChatMsgForm.MsgContext msgContext);

    int signMsg(List<String> msgIds);

    UnSignMsgVo findUnSignMsgList(String receiverId);
}
