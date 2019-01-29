package com.ruoyi;

import com.ruoyi.framework.util.redis.service.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * @Author: wtao
 * @Date: 2019-01-29 13:29
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RuoYiApplication.class)
public class RedisTest {
    @Autowired
    RedisUtil redisUtil;

    @Test
    public void redisMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("key1", "v1");
        map.put("key2", "v2");
        map.put("key3", "v3");

        Long time = new Date().getTime() / (60 * 1000) * (60 * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date start = new Date();
        for (int i = 0; i < 43200; i++) {
            String key = sdf.format(new Date(time));
            redisUtil.hmset(key, map);
            time += 60 * 1000;
        }
        Date end = new Date();
        long sub = end.getTime() - start.getTime();
        System.out.println("redis 写入完成，用时：" + sub + "ms");
    }
}
