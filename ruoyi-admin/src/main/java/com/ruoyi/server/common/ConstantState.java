package com.ruoyi.server.common;

import com.ruoyi.server.domain.ResolveRecord;
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
public final class ConstantState {
    public static Map<String, Map<String, ResolveRecord>> codeRecord = new HashMap<>();
    public static Map<String, String> registeredCode = new HashMap<>();
    public static Map<String, String> codeCron = new HashMap<>();
    public static Map<String, String> registeredIp = new HashMap<>();
    public static Map<String, ChannelHandlerContext> registeredCtx = new HashMap<>();
    public static Map<ChannelHandlerContext, Map<String, ResolveRecord>> ctxRecord = new HashMap<>();
    public static Map<ChannelHandlerContext, Integer> ctxIndex = new HashMap<>();
    public static Map<ChannelHandlerContext, Date> ctxHeartTime = new HashMap<>();
    public static Map<ChannelHandlerContext, String> ctxName = new HashMap<>();
    public static Map<String, String> codeName = new HashMap<>();
    public static Map<ChannelHandlerContext, Map<Integer, Map<String, List<String>>>> ctxDataFormat = new HashMap<>();
    public static Map<SysCollectionPoint, String> recordValues = new HashMap<>();
}
