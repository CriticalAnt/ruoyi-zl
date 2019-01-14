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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
        for (Map.Entry<ChannelHandlerContext, Map<String, ResolveRecord>> entry : ps.entrySet()) {
            for (Map.Entry<String, ResolveRecord> pEntry : entry.getValue().entrySet()) {
                for (SysCollectionPoint point : pEntry.getValue().getPoints()) {
                    for (SysCollectionPoint p : points) {
                        if (p.getPointId() == point.getPointId()
                                && p.getSlaveId() == point.getSlaveId()
                                && p.getDevId() == point.getDevId()) {
                            try {
                                resSet.remove(p);
                            } catch (Exception e) {
                            }
                            p = point;
                            resSet.add(p);
                        }
                    }
                }
            }
        }
        modelMap.put("devs", devs);
        modelMap.put("points", resSet);
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
}
