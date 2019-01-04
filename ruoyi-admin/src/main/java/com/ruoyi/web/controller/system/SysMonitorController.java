package com.ruoyi.web.controller.system;

import com.ruoyi.framework.web.page.TableDataInfo;
import com.ruoyi.server.common.Constant2;
import com.ruoyi.system.domain.SysDevice;
import com.ruoyi.system.service.ISysDeviceService;
import com.ruoyi.web.core.base.BaseController;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @RequiresPermissions("system:monitor:view")
    @GetMapping()
    public String dic() {
        return prefix + "/monitor";
    }

    @PostMapping("list")
    @ResponseBody
    public TableDataInfo list() {
        startPage();
        List<Map<String, String>> mapList = new ArrayList<>();
        List<SysDevice> devices = deviceService.findAll();
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, String> entry : Constant2.registeredCode.entrySet())
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
