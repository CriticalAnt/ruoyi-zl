package com.ruoyi.server.common;

import com.ruoyi.system.domain.SysCollectionPoint;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: wtao
 * @Date: 2018-12-30 3:08
 * @Version 1.0
 */
public final class Constant2 {
    public static Map<String, List<SysCollectionPoint>> codePoint = new HashMap<>();
    public static Map<String, String> registeredCode = new HashMap<>();
    public static Map<String, String> registeredIp = new HashMap<>();
    public static Map<String, ChannelHandlerContext> registeredCtx = new HashMap<>();
    public static Map<ChannelHandlerContext, List<SysCollectionPoint>> ctxPoints = new HashMap<>();
    public static Map<ChannelHandlerContext, Integer> ctxIndex = new HashMap<>();
    public static Map<ChannelHandlerContext, Date> ctxHeartTime = new HashMap<>();
}
