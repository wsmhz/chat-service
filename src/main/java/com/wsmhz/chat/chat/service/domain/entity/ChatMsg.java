package com.wsmhz.chat.chat.service.domain.entity;

import com.wsmhz.common.business.domain.Domain;
import lombok.*;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created By tangbj On 2019/6/12
 * Description:
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "chat_msg")
public class ChatMsg extends Domain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 消息标识
     */
    private String msgId;
    /**
     * 发送者的用户id
     */
    private String senderId;
    /**
     * 接受者的用户id
     */
    private String receiverId;
    /**
     * 消息内容
     */
    private String context;
    /**
     * 消息类型
     */
    private Integer msgType;
    /**
     * 消息是否签收状态
     *    1：签收
     *    0：未签收
     */
    private Integer signFlag;

    @Builder
    public ChatMsg(Date createDate, Date updateDate, Date deleteDate, String msgId, String senderId, String receiverId, String context, Integer msgType, Integer signFlag) {
        super(createDate, updateDate, deleteDate);
        this.msgId = msgId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.context = context;
        this.msgType = msgType;
        this.signFlag = signFlag;
    }
}
