package com.ruoyi.server.utils;

import com.ruoyi.quartz.QuartzManager;
import com.ruoyi.server.common.ConstantState;
import com.ruoyi.websocket.server.WebSocketServer;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.SchedulerException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: wtao
 * @Date: 2018-12-30 3:27
 * @Version 1.0
 */
public class UtilsRegister {

    private static Logger logger = LogManager.getLogger(UtilsRegister.class);

    public static boolean isRegistered(String code, String address, ChannelHandlerContext ctx) throws SchedulerException, IOException {
        if (ConstantState.registeredIp.containsKey(address)) {
            return true;
        }
        if ("0".equals(ConstantState.registeredCode.get(code))) {
            ConstantState.registeredIp.put(address, code);
            ConstantState.registeredCode.put(code, "1");
            ConstantState.registeredCtx.put(code, ctx);
            ConstantState.ctxRecord.put(ctx, ConstantState.codeRecord.get(code));
            ConstantState.ctxIndex.put(ctx, 0);
            String cron = ConstantState.codeCron.get(code);
            String adr = ctx.channel().remoteAddress().toString();
            Map<String, Object> map = new HashMap<>();
            map.put(adr, ctx);
            try {
                QuartzManager quartzManager = QuartzManager.getInstance();
                quartzManager.addJob(ctx.channel().remoteAddress().toString(), code
                        , cron, map);
            } catch (Exception e) {
                logger.error("UtilsRegister::isRegistered:" + e.getMessage());
            }
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    WebSocketServer.sendMessage();
                } catch (InterruptedException e) {
                    logger.error("UtilsRegister::isRegistered:" + e.getMessage());
                }
            }).start();
            return true;
        }
        return false;
    }
}
