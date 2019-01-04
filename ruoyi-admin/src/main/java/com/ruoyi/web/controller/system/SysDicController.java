package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.base.AjaxResult;
import com.ruoyi.common.enums.*;
import com.ruoyi.framework.web.page.TableDataInfo;
import com.ruoyi.system.domain.SysDatapoint;
import com.ruoyi.system.service.ISysDatapointService;
import com.ruoyi.web.core.base.BaseController;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: wtao
 * @Date: 2018-12-25 22:57
 * @Version 1.0
 */
@Controller
@RequestMapping("system/dic")
public class SysDicController extends BaseController {

    private String prefix = "system/dic";

    @Autowired
    private ISysDatapointService datapointService;

    @RequiresPermissions("system:dic:view")
    @GetMapping()
    public String dic() {
        return prefix + "/dic";
    }

    @RequiresPermissions("system:dic:list")
    @GetMapping("/list/{id}")
    public String dicList(@PathVariable("id") Long id, ModelMap mmap) {
        mmap.put("id", id);
        return prefix + "/dic";
    }


    @RequiresPermissions("system:dic:list")
    @PostMapping("/list/{id}")
    @ResponseBody
    public TableDataInfo list(@PathVariable("id") int id, SysDatapoint data) {
        startPage();
        List<Map<String, String>> mapList = new ArrayList<>();
        List<SysDatapoint> list;
        if (id == 0)
            list = datapointService.findAll();
        else
            list = datapointService.selectByTempId(Long.valueOf(id));
        for (SysDatapoint point : list) {
            if (id != 0 && point.getTempId() != id)
                continue;
            Map<String, String> map = new HashMap<>();
            map.put("id", String.valueOf(point.getId()));
            map.put("tempId", String.valueOf(point.getTempId()));
            map.put("pointName", point.getPointName());
            map.put("dataType", DataType.getDescByCode(point.getDataType()));
            map.put("registerAdr", point.getRegisterAdr());
            map.put("valueType", ValueType.getDescByCode(point.getValueType()));
            map.put("decimalLen", String.valueOf(point.getDecimalLen()));
            map.put("readType", ReadType.getDescByCode(point.getReadType()));
            map.put("registerLen", point.getRegisterLen());
            map.put("unit", point.getUnit());
            map.put("formula", point.getFormula());
            map.put("storageType", StorageType.getDescByCode(point.getStorageType()));
            mapList.add(map);
        }
        return getDataTable(mapList, list);
    }

    @RequiresPermissions("system:dic:remove")
    @Log(title = "数据管理", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        int num;
        try {
            num = datapointService.deleteDatapointByIds(ids);
        } catch (Exception e) {
            return error(e.getMessage());
        }
        if (num > 0)
            updatePoints();
        return toAjax(num);
    }

    @GetMapping("/edit/{dicId}")
    public String edit(@PathVariable("dicId") Long dicId, ModelMap mmap) {
        SysDatapoint datapoint = datapointService.selectById(dicId);
        mmap.put("datapoint", datapoint);
        return prefix + "/edit";
    }

    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult edit(SysDatapoint datapoint) {
        int num = datapointService.update(datapoint);
        if (num > 0)
            updatePoints();
        return toAjax(num);
    }
}
