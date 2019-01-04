package com.ruoyi.server.common;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: wtao
 * @Date: 2018/12/13 20:14
 * @Version 1.0
 */
public class ChannelService {

    private static Map<String, ChannelHandlerContext> channels = new ConcurrentHashMap<>();

    public static void addChannel(String id, ChannelHandlerContext s) {
        channels.put(id, s);
    }

    public static void removeChannel(String id) {
        channels.remove(id);
    }

    public static Map<String, ChannelHandlerContext> getChannels() {
        return channels;
    }
}
