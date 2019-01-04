package com.ruoyi.server.utils;

import com.ruoyi.server.common.Constant2;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @Author: wtao
 * @Date: 2018-12-30 3:27
 * @Version 1.0
 */
public class UtilsRegister {

    private static Logger logger = LogManager.getLogger(UtilsRegister.class);

    public static boolean isRegistered(String code, String address, ChannelHandlerContext ctx) {
        if (Constant2.registeredIp.containsKey(address)) {
            return true;
        }
        if ("0".equals(Constant2.registeredCode.get(code))) {
            Constant2.registeredIp.put(address, code);
            Constant2.registeredCode.put(code, "1");
            Constant2.registeredCtx.put(code, ctx);
            Constant2.ctxPoints.put(ctx, Constant2.codePoint.get(code));
            Constant2.ctxIndex.put(ctx, 0);
            return true;
        }
        return false;
    }
}
