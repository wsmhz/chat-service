package com.wsmhz.chat.chat.service.enums;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created By tangbj On 2019/6/12
 * Description:
 */
public enum MsgSignEnum {

    UN_SIGN(0, "未签收"),
    SIGNED(1, "已签收");

    private Integer id;
    private String desc;

    MsgSignEnum(Integer id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public Integer getId() {
        return id;
    }

    public String getDesc() {
        return desc;
    }

    public static Map<Integer, MsgSignEnum> enumMap = new HashMap<>();

    static {
        for (MsgSignEnum value : MsgSignEnum.values()) {
            enumMap.put(value.id, value);
        }
    }

    public static MsgSignEnum valueOf(Integer type) {
        return Optional.ofNullable(enumMap.get(type)).orElseThrow(() -> new IllegalArgumentException("未知的MsgSignEnum类型：" + type + "!"));
    }
}
