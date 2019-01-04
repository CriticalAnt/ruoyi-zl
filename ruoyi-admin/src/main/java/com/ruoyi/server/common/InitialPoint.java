package com.ruoyi.server.common;

import com.ruoyi.system.domain.SysCollectionPoint;
import com.ruoyi.system.domain.SysDevice;
import com.ruoyi.system.mapper.SysCollectionPointMapper;
import com.ruoyi.system.service.ISysDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: wtao
 * @Date: 2018-12-30 2:01
 * @Version 1.0
 */
@Component
public class InitialPoint implements CommandLineRunner {

    @Autowired
    private ISysDeviceService deviceService;

    @Autowired
    private SysCollectionPointMapper pointMapper;


    public void init() {
        Constant2.codePoint.clear();
        Constant2.registeredCode.clear();
        List<SysDevice> devices = deviceService.findAll();
        List<SysCollectionPoint> points = pointMapper.findAll();
        for (SysDevice device : devices) {
            List<SysCollectionPoint> list = new ArrayList<>();
            for (SysCollectionPoint point : points) {
                if (point.getDevId() == device.getId()) {
                    list.add(point);
                }
            }
            Constant2.codePoint.put(device.getCode(), list);
            Constant2.registeredCode.put(device.getCode(), "0");
        }
    }

    @Override
    public void run(String... args) throws Exception {
        init();
    }
}
