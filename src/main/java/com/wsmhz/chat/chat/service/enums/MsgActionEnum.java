package com.wsmhz.chat.chat.service.enums;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created By tangbj On 2019/6/12
 * Description: 发送消息的动作 枚举
 */
public enum  MsgActionEnum {

    CONNECT(1, "第一次(或重连)初始化连接"),
    CHAT(2, "聊天"),
    SIGNED(3, "消息签收"),
    KEEP_ALIVE(4, "客户端保持心跳");

    private Integer id;
    private String desc;

    MsgActionEnum(Integer id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public Integer getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }

    public static Map<Integer, MsgActionEnum> enumMap = new HashMap<>();

    static {
        for (MsgActionEnum value : MsgActionEnum.values()) {
            enumMap.put(value.id, value);
        }
    }

    public static MsgActionEnum valueOf(Integer type) {
        return Optional.ofNullable(enumMap.get(type)).orElseThrow(() -> new IllegalArgumentException("未知的MsgActionEnum类型：" + type + "!"));
    }
}
