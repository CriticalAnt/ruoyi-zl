package com.ruoyi.server.task;

import com.ruoyi.common.json.JSON;
import com.ruoyi.framework.util.redis.service.RedisUtil;
import com.ruoyi.server.common.ConstantState;
import com.ruoyi.system.domain.CollectionResult;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: wtao
 * @Date: 2019-01-01 23:44
 * @Version 1.0
 */
@Component
public class ScheduledActive {

    @Autowired
    RedisUtil redisUtil;

    private boolean done = false;

    @Scheduled(cron = "0/10 * * * * *")
    public void checkChannelHandlerContext() {
        Map<ChannelHandlerContext, Date> map = new HashMap<>();
        synchronized (ConstantState.ctxHeartTime) {
            for (Map.Entry<ChannelHandlerContext, Date> entry : ConstantState.ctxHeartTime.entrySet()) {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        for (Map.Entry<ChannelHandlerContext, Date> entry : map.entrySet()) {
            if (new Date().getTime() - entry.getValue().getTime() > 120 * 1000) {
                entry.getKey().close();
            }
        }
    }

    @Scheduled(cron = "0/10 * * * * *")
    public void redisTest() throws Exception {
        if (done)
            return;
        HashMap<String, Object> map = new HashMap<>();
        CollectionResult point = new CollectionResult();
        point.setValue("123456.00");
        Long time = new Date().getTime() / (60 * 1000) * (60 * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date start = new Date();
        String key = JSON.marshal(point);
        for (int k = 0; k < 500; k++) {
            long temp = time + 60 * 1000 * k;
            String date = sdf.format(new Date(temp));
            List<Object> list = new ArrayList<>();
            for (int i = 0; i < 40; i++) {
                for (int j = 0; j < 100; j++) {
                    list.add(key);
                }
            }
            redisUtil.lSet(date, list);
            list.clear();
        }
        Date end = new Date();
        long sub = end.getTime() - start.getTime();
        System.out.println("redis 写入完成，用时：" + sub + "ms");
        done = true;
    }
}
