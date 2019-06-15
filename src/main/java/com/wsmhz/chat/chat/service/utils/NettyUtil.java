package com.wsmhz.chat.chat.service.utils;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

/**
 * Created By tangbj On 2019/6/15
 * Description:
 */
@Slf4j
public class NettyUtil {

    // 记录所有客户端的Channel
    private static ChannelGroup globalUsers = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    // 记录用户id和channel的关联关系
    private static HashMap<String, Channel> relation = new HashMap<>();

    public static void addUser(Channel channel){
        globalUsers.add(channel);
    }

    public static void removeUser(Channel channel){
        globalUsers.remove(channel);
    }

    public static int getUserSize(){
        return globalUsers.size();
    }

    public static void putRelation(String senderId, Channel channel) {
        relation.put(senderId, channel);
    }

    public static Channel getRelation(String senderId) {
        return relation.get(senderId);
    }

    public static void printRelation() {
        for (HashMap.Entry<String, Channel> entry : relation.entrySet()) {
            log.info("UserId: " + entry.getKey()
                    + ", ChannelId: " + entry.getValue().id().asLongText());
        }
    }

    public static Channel validateChannel(String userChannelId){
        Channel channel = getRelation(userChannelId);
        if (channel == null) {
            // TODO 用户离线，推送消息
            return null;
        } else {
            Channel findChannel = globalUsers.find(channel.id());
            if (findChannel != null) {
                return channel;
            }
            // TODO 用户离线，推送消息
            return null;
        }
    }
}
