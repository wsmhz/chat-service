package com.wsmhz.chat.chat.service.controller;

import com.wsmhz.chat.chat.service.api.domain.form.UnSignMsgForm;
import com.wsmhz.chat.chat.service.api.domain.vo.UnSignMsgVo;
import com.wsmhz.chat.chat.service.netty.TextFrameHandler;
import com.wsmhz.chat.chat.service.service.ChatMsgService;
import com.wsmhz.common.business.response.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

/**
 * Created By tangbj On 2019/6/12
 * Description:
 */
@Controller
public class ChatMsgController {

    @Autowired
    private ChatMsgService chatMsgService;

    @GetMapping("/chat")
    public String test() {
        return "/chat.html";
    }

    @GetMapping("/chat2")
    public String test2() {
        return "/chat2.html";
    }

    @ResponseBody
    @PostMapping("/msg/unsign")
    public ServerResponse findUnSignMsgList(@RequestBody @Valid UnSignMsgForm unSignMsgForm){
        return ServerResponse.createBySuccess(chatMsgService.findUnSignMsgList(unSignMsgForm.getReceiverId()));
    }

//    @GetMapping("/msg/upload")
//    public ServerResponse upload(){
//        TextFrameHandler textFrameHandler = new TextFrameHandler();
//        textFrameHandler.channelRead0();
//        return ServerResponse.createBySuccess();
//    }
}
