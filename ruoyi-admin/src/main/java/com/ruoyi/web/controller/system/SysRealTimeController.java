package com.ruoyi.web.controller.system;

import com.ruoyi.server.common.ConstantState;
import com.ruoyi.server.domain.ResolveRecord;
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
    public Object list(Long devId, String slaveIds, String pointIds) {
        Map<String, Object> map = new HashMap<>();
//        Integer[] pIds = Convert.toIntArray(pointIds);
//        Integer[] sIds = Convert.toIntArray(slaveIds);
//        List<Integer> pIdList = Arrays.asList(pIds);
//        List<Integer> sIdList = Arrays.asList(sIds);
        String[] Ids = pointIds.split(",");
        Map<Integer, List<Integer>> mapIds = new HashMap<>();
        for (String id : Ids) {
            Integer sId = Integer.valueOf(id.split("_")[0]);
            Integer pId = Integer.valueOf(id.split("_")[1]);
            if (mapIds.containsKey(sId)) {
                mapIds.get(sId).add(pId);
            } else {
                mapIds.put(sId, new ArrayList<Integer>() {{
                    add(pId);
                }});
            }
        }
        if (devId == null || mapIds.size() == 0) {
            map.put("msg", "设备ID或从机ID不能为空");
            return map;
        }
        if (devId < 0) {
            map.put("msg", "设备ID或从机ID不能为空");
            return map;
        }
//        if (pIds.length == 0) {
//            map.put("msg", "数据点ID不能为空");
//            return map;
//        }
        Map<ChannelHandlerContext, Map<String, ResolveRecord>> points = new HashMap<>();
        List<String> arrtitle = new ArrayList<>();
        Map<String, String> yDatas = new HashMap<>();
        synchronized (ConstantState.ctxRecord) {
            for (Map.Entry<ChannelHandlerContext, Map<String, ResolveRecord>> entry : ConstantState.ctxRecord.entrySet())
                points.put(entry.getKey(), entry.getValue());
        }
        List<Map<String, Object>> series = new ArrayList<>();
        Map<String, SysCollectionPoint> results = new HashMap<>();
        Map<String, String> mapKeys = new HashMap<>();
        for (Map.Entry<ChannelHandlerContext, Map<String, ResolveRecord>> entry : points.entrySet()) {
            for (Map.Entry<String, ResolveRecord> pEntry : entry.getValue().entrySet()) {
                for (SysCollectionPoint point : pEntry.getValue().getPoints()) {
                    List<Integer> pIds = mapIds.get(point.getSlaveId());
                    if (pIds == null)
                        continue;
                    if (pIds.contains(point.getPointId())) {
                        String key = point.getSlaveId() + "-" + point.getPointId();
                        String strKey = point.getSlaveName() + ":" + point.getPointName();
                        results.put(key, point);
                        mapKeys.put(key, strKey);
                    }
                }
            }
        }
        for (Map.Entry<String, SysCollectionPoint> entry : results.entrySet()) {
            Map<String, Object> resMap = new HashMap<>();
            String value = entry.getValue().getValue();
            String[] data = new String[]{value};
            String name = mapKeys.get(entry.getKey());
            resMap.put("name", name);
            resMap.put("type", "line");
            resMap.put("data", data);
            series.add(resMap);
        }
        map.put("series", series);
        return map;
    }
}
