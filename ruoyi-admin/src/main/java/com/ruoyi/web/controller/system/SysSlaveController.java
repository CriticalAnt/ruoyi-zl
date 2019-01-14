package com.ruoyi.web.controller.system;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.base.AjaxResult;
import com.ruoyi.framework.web.page.TableDataInfo;
import com.ruoyi.system.domain.SysSlave;
import com.ruoyi.system.service.ISysSlaveService;
import com.ruoyi.web.core.base.BaseController;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author: wtao
 * @Date: 2018-12-28 2:33
 * @Version 1.0
 */
@RequestMapping("system/slave")
@Controller
public class SysSlaveController extends BaseController {

    private String prefix = "system/slave";

    @Autowired
    private ISysSlaveService slaveService;

    @ResponseBody
    @PostMapping("/add")
    public AjaxResult add(@RequestParam("list") String pointJson) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JavaType jt = mapper.getTypeFactory().constructParametricType(ArrayList.class, SysSlave.class);
        List<SysSlave> slaves = mapper.readValue(pointJson, jt);
        Set<Integer> set = new HashSet<>();
        int num = 0;
        for (SysSlave slave : slaves) {
            num += slaveService.insert(slave);
            set.add(slave.getTempId());
        }
        if (num == slaves.size())
            updatePointsByDevId(set);
        return toAjax(num == slaves.size() ? 1 : 0);
    }

//    @RequiresPermissions("system:slave:list")
    @PostMapping("/list/{id}")
    @ResponseBody
    public TableDataInfo list(@PathVariable("id") Long id, SysSlave slave) {
        startPage();
        List<SysSlave> list = slaveService.selectByDevId(id);
        return getDataTable(list);
    }

    @GetMapping("/list/{id}")
    public String slave(@PathVariable("id") Long id, ModelMap modelMap) {
        modelMap.put("devId", id);
        return prefix + "/slave";
    }

    @PostMapping("remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        int num;
        List<SysSlave> slaves;
        try {
            num = slaveService.deleteByIds(ids);
            slaves = slaveService.selectByIds(ids);
        } catch (Exception e) {
            return error(e.getMessage());
        }
        if (num > 0) {
            Set<Integer> set = new HashSet<>();
            for (SysSlave slave : slaves) {
                set.add(slave.getTempId());
            }
            updatePointsByDevId(set);
        }
        return toAjax(num);
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap) {
        SysSlave slave = slaveService.selectById(id);
        mmap.put("slave", slave);
        return prefix + "/edit";
    }

    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult edit(SysSlave slave) {
        int num = slaveService.update(slave);
        if (num > 0)
            updatePointsByDevId(new HashSet<Integer>() {{
                add(slave.getTempId());
            }});
        return toAjax(num);
    }
}
