package com.ruoyi.web.controller.system;

import com.google.common.collect.Sets;
import com.ruoyi.framework.web.page.TableDataInfo;
import com.ruoyi.server.common.ConstantState;
import com.ruoyi.server.domain.ResolveRecord;
import com.ruoyi.system.domain.SysCollectionPoint;
import com.ruoyi.system.domain.SysDevice;
import com.ruoyi.system.mapper.SysCollectionPointMapper;
import com.ruoyi.system.service.ISysDeviceService;
import com.ruoyi.web.core.base.BaseController;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: wtao
 * @Date: 2019-01-02 4:43
 * @Version 1.0
 */
@Controller
@RequestMapping("system/monitor")
public class SysMonitorController extends BaseController {

    private String prefix = "system/monitor";

    @Autowired
    ISysDeviceService deviceService;

    @Autowired
    SysCollectionPointMapper pointMapper;

    //    @RequiresPermissions("system:monitor:view")
    @GetMapping()
    public String dic(ModelMap modelMap) {
        List<SysDevice> devs = deviceService.findAll();
        List<SysCollectionPoint> points = pointMapper.findAll();
//        List<SysCollectionPoint> res = new ArrayList<>();
        Set<SysCollectionPoint> resSet = Sets.newHashSet();
        Map<ChannelHandlerContext, Map<String, ResolveRecord>> ps = new HashMap<>();
        synchronized (ConstantState.ctxRecord) {
            for (Map.Entry<ChannelHandlerContext, Map<String, ResolveRecord>> entry : ConstantState.ctxRecord.entrySet())
                ps.put(entry.getKey(), entry.getValue());
        }
        for (SysCollectionPoint p : points) {
            resSet.add(p);
        }
        for (Map.Entry<ChannelHandlerContext, Map<String, ResolveRecord>> entry : ps.entrySet()) {
            for (Map.Entry<String, ResolveRecord> pEntry : entry.getValue().entrySet()) {
                for (SysCollectionPoint point : pEntry.getValue().getPoints()) {
                    for (SysCollectionPoint p : points) {
                        if (p.getPointId() == point.getPointId()
                                && p.getSlaveId() == point.getSlaveId()
                                && p.getDevId() == point.getDevId()) {
                            resSet.remove(p);
                            p = point;
                            resSet.add(p);
                        }
                    }
                }
            }
        }
        List<SysCollectionPoint> list = new ArrayList<>(resSet);
        Collections.sort(list, (o1, o2) -> {
            int x = o1.getDevId() - o2.getDevId();
            int y = o1.getSlaveId() - o2.getSlaveId();
            int z = Integer.valueOf(o1.getRegisterAdr())
                    - Integer.valueOf(o2.getRegisterAdr());
            if (x == 0) {
                if (y == 0) {
                    return z;
                }
                return y;
            }
            return x;
        });
        modelMap.put("devs", devs);
        modelMap.put("points", list);
        return prefix + "/monitor";
    }

    @PostMapping("list")
    @ResponseBody
    public TableDataInfo list() {
        startPage();
        List<Map<String, String>> mapList = new ArrayList<>();
        List<SysDevice> devices = deviceService.findAll();
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, String> entry : ConstantState.registeredCode.entrySet())
            map.put(entry.getKey(), entry.getValue());
        for (SysDevice device : devices) {
            Map<String, String> res = new HashMap<>();
            res.put("id", String.valueOf(device.getId()));
            res.put("devNum", device.getDevNum());
            res.put("devName", device.getDevName());
            if ("1".equals(map.get(device.getCode()))) {
                res.put("status", "1");
            } else {
                res.put("status", "0");
            }
            mapList.add(res);
        }
        return getDataTable(mapList, devices);
    }

    @PostMapping("list/{devId}")
    @ResponseBody
    public TableDataInfo list(@PathVariable int devId) {
        List<SysDevice> devs = deviceService.findAll();
        List<SysCollectionPoint> points = pointMapper.selectByDevId(devId);
//        List<SysCollectionPoint> res = new ArrayList<>();
        Set<SysCollectionPoint> resSet = Sets.newHashSet();
        Map<ChannelHandlerContext, Map<String, ResolveRecord>> ps = new HashMap<>();
        synchronized (ConstantState.ctxRecord) {
            for (Map.Entry<ChannelHandlerContext, Map<String, ResolveRecord>> entry : ConstantState.ctxRecord.entrySet())
                ps.put(entry.getKey(), entry.getValue());
        }
        for (SysCollectionPoint p : points) {
            resSet.add(p);
        }
        for (Map.Entry<ChannelHandlerContext, Map<String, ResolveRecord>> entry : ps.entrySet()) {
            for (Map.Entry<String, ResolveRecord> pEntry : entry.getValue().entrySet()) {
                for (SysCollectionPoint point : pEntry.getValue().getPoints()) {
                    for (SysCollectionPoint p : points) {
                        if (p.getPointId() == point.getPointId()
                                && p.getSlaveId() == point.getSlaveId()
                                && p.getDevId() == point.getDevId()) {
                            resSet.remove(p);
                            p = point;
                            resSet.add(p);
                        }
                    }
                }
            }
        }
        startPage();
        List<SysCollectionPoint> list = new ArrayList<>(resSet);
        Collections.sort(list, (o1, o2) -> {
            int x = o1.getDevId() - o2.getDevId();
            int y = o1.getSlaveId() - o2.getSlaveId();
            int z = Integer.valueOf(o1.getRegisterAdr())
                    - Integer.valueOf(o2.getRegisterAdr());
            if (x == 0) {
                if (y == 0) {
                    return z;
                }
                return y;
            }
            return x;
        });
        List<Map<String, String>> result = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        for (SysCollectionPoint point : list) {
            Map<String, String> map = new HashMap<>();
            map.put("pointName", point.getPointName());
            map.put("slaveName", point.getSlaveName());
            if (point.getValue() == null) {
                map.put("value", "-");
            } else {
                map.put("value", point.getValue() + point.getUnit());
            }
            if (point.getUpdateTime() == null) {
                map.put("updateTime", "-");
            } else {
                map.put("updateTime", format.format(point.getUpdateTime()));
            }
            result.add(map);
        }
        return getDataTable(result);
    }
}
