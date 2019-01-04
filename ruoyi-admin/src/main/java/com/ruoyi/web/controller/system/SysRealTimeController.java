package com.ruoyi.web.controller.system;

import com.ruoyi.server.common.Constant2;
import com.ruoyi.system.domain.SysCollectionPoint;
import com.ruoyi.system.domain.SysDevice;
import com.ruoyi.system.domain.SysSlave;
import com.ruoyi.system.mapper.SysCollectionPointMapper;
import com.ruoyi.system.service.ISysDatapointService;
import com.ruoyi.system.service.ISysDeviceService;
import com.ruoyi.system.service.ISysSlaveService;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: wtao
 * @Date: 2019-01-02 20:44
 * @Version 1.0
 */
@Controller
@RequestMapping("system/realTime")
public class SysRealTimeController {

    private String prefix = "system/realTime";

    @Autowired
    ISysDeviceService deviceService;

    @Autowired
    ISysSlaveService slaveService;

    @Autowired
    ISysDatapointService datapointService;

    @Autowired
    SysCollectionPointMapper collectionPointMapper;

    @GetMapping
    public String realTime(ModelMap modelMap) {
        List<SysDevice> devices = deviceService.findAll();
        List<SysSlave> slaves = slaveService.findAll();
        List<SysCollectionPoint> points = collectionPointMapper.findAll();
        modelMap.put("devices", devices);
        modelMap.put("slaves", slaves);
        modelMap.put("points", points);
        return prefix + "/realTime";
    }

    @GetMapping("list")
    @ResponseBody
    public Object list(Long devId, Long slaveId, Long pointId) {
        Map<String, Object> map = new HashMap<>();
        if (devId == null || slaveId == null || pointId == null) {
            map.put("msg", "id不能为空");
            return map;
        }
        if (devId < 0 || slaveId < 0 || pointId < 0) {
            map.put("msg", "id不能小于0");
            return map;
        }
        Map<ChannelHandlerContext, List<SysCollectionPoint>> points = new HashMap<>();
        List<String> arrtitle = new ArrayList<>();
        Map<String, String> yDatas = new HashMap<>();
        synchronized (Constant2.ctxPoints) {
            for (Map.Entry<ChannelHandlerContext, List<SysCollectionPoint>> entry : Constant2.ctxPoints.entrySet())
                points.put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<ChannelHandlerContext, List<SysCollectionPoint>> entry : points.entrySet()) {
            for (SysCollectionPoint point : entry.getValue()) {
                if (point.getDevId() == devId && point.getSlaveId() == slaveId && point.getPointId() == pointId) {
                    String value = point.getValue();
//                    if (value != null && value.length() > 1)
//                        value = value.substring(0, value.length() - 1);
                    arrtitle.add(point.getPointName());
                    yDatas.put("data", value);
                    yDatas.put("name", point.getPointName());
                    yDatas.put("type", "line");
                }
            }
        }
        if (arrtitle.size() > 0) {
            map.put("arrtitle", arrtitle.get(0));
            map.put("yDatas", yDatas.get(0));
            map.put("arrtitle", 123);
            map.put("yDatas", yDatas);
            return map;
        }
        map.put("msg", "has no data");
        return map;
//        m.put("name", "名称测试");
//        m.put("type", "line");
//        m.put("data", 456);
//        map.put("arrtitle", 123);
//        map.put("yDatas", m);
//        return map;
    }
}
