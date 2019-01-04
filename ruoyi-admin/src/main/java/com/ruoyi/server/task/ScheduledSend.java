package com.ruoyi.server.task;

import com.ruoyi.server.common.Constant2;
import com.ruoyi.system.domain.SysCollectionPoint;
import com.ruoyi.system.domain.SysDevice;
import com.ruoyi.system.service.ISysDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: wtao
 * @Date: 2019-01-02 2:33
 * @Version 1.0
 */
@Component
public class ScheduledSend {

    @Autowired
    ISysDeviceService deviceService;

    @Scheduled(cron = "0 0/1 * * * *")
    public void send() {
        List<SysDevice> devices = deviceService.findAll();
        List<SysDevice> devsOnline = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        Map<SysCollectionPoint, String> res = new HashMap<>();
        synchronized (Constant2.registeredCode) {
            for (Map.Entry<String, String> entry : Constant2.registeredCode.entrySet())
                map.put(entry.getKey(), entry.getValue());
        }
        for (SysDevice device : devices) {
            if (map.get(device.getCode()).equals("1"))
                devsOnline.add(device);
        }

    }
}
