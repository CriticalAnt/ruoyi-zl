package com.ruoyi.server.task;

import com.ruoyi.server.common.ConstantState;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: wtao
 * @Date: 2019-01-01 23:44
 * @Version 1.0
 */
@Component
public class ScheduledActive {

    @Scheduled(cron = "0/10 * * * * *")
    public void checkChannelHandlerContext() {
        Map<ChannelHandlerContext, Date> map = new HashMap<>();
        synchronized (ConstantState.ctxHeartTime) {
            for (Map.Entry<ChannelHandlerContext, Date> entry : ConstantState.ctxHeartTime.entrySet()) {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        for (Map.Entry<ChannelHandlerContext, Date> entry : map.entrySet()) {
            if (new Date().getTime() - entry.getValue().getTime() > 3000 * 1000) {
                entry.getKey().close();
            }
        }
    }
}
