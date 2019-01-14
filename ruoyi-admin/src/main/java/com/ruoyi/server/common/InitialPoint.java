package com.ruoyi.server.common;

import com.ruoyi.server.domain.ResolveRecord;
import com.ruoyi.system.domain.SysCollectionPoint;
import com.ruoyi.system.domain.SysDevice;
import com.ruoyi.system.mapper.SysCollectionPointMapper;
import com.ruoyi.system.service.ISysDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        ConstantState.codeRecord.clear();
        ConstantState.registeredCode.clear();
        List<SysDevice> devices = deviceService.findAll();
        List<SysCollectionPoint> points = pointMapper.findAll();
        for (SysDevice device : devices) {
            Map<String, ResolveRecord> map = new HashMap<>();
            for (SysCollectionPoint point : points) {
                if (point.getDevId() == device.getId()) {
                    int equNum = point.getEquNum();
                    int code = Integer.valueOf(point.getRegisterAdr()) / 10000;
                    code = code == 3 ? 4 : 3;
                    String key = equNum + "-" + code;
                    if (!map.containsKey(key)) {
                        ResolveRecord record = new ResolveRecord(new ArrayList<SysCollectionPoint>() {{
                            add(point);
                        }});
                        map.put(key, record);
                    } else
                        map.get(key).getPoints().add(point);
                }
            }
            ConstantState.codeRecord.put(device.getCode(), map);
            ConstantState.codeCron.put(device.getCode(), device.getFrequency());
            ConstantState.registeredCode.put(device.getCode(), "0");
        }
    }

    @Override
    public void run(String... args) throws Exception {
        init();
    }
}
