package com.ruoyi.web.controller.system;

import com.ruoyi.common.base.AjaxResult;
import com.ruoyi.framework.web.page.TableDataInfo;
import com.ruoyi.system.domain.SysDevice;
import com.ruoyi.system.service.impl.SysDeviceServiceImpl;
import com.ruoyi.web.core.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * @Author: wtao
 * @Date: 2018-12-26 15:55
 * @Version 1.0
 */
@Controller
@RequestMapping("system/device")
public class SysDeviceController extends BaseController {

    private String prefix = "system/device";

    @Autowired
    private SysDeviceServiceImpl deviceService;

    @GetMapping
    public String device() {
        return prefix + "/list";
    }

    @PostMapping("list")
    @ResponseBody
    public TableDataInfo list() {
        startPage();
        List<SysDevice> list = deviceService.findAll();
        return getDataTable(list);
    }

    @PostMapping("remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        removeDevice(ids);
        try {
            return toAjax(deviceService.deleteDeviceByIds(ids));
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    @GetMapping("/add/{devId}")
    public String add(@PathVariable("devId") Long devId, ModelMap mmap) {
        mmap.put("devId", devId);
        return prefix + "/addSlave";
    }

    @GetMapping("/edit/{devId}")
    public String edit(@PathVariable("devId") Long devId, ModelMap modelMap) {
        modelMap.put("device", deviceService.selectById(devId));
        return prefix + "/edit";
    }

    @GetMapping("/add")
    public String add() {
        return prefix + "/add";
    }

    @PostMapping("/add")
    @ResponseBody
    public AjaxResult add(SysDevice device) {
        device.setCode(UUID.randomUUID().toString().replaceAll("-", ""));
        int num;
        try {
            num = deviceService.insert(device);
        } catch (Exception e) {
            return error(e.getMessage());
        }
        if (num > 0)
            addDevice(device);
        return toAjax(num);
    }

    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult edit(SysDevice device) {
        try {
            int num = deviceService.update(device);
            updateDevice(deviceService.selectById(Long.valueOf(device.getId())));
            return toAjax(num);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }
}
