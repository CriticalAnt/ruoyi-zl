package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.base.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.framework.web.page.TableDataInfo;
import com.ruoyi.system.domain.SysRole;
import com.ruoyi.system.domain.SysTemplet;
import com.ruoyi.system.service.ISysTempletService;
import com.ruoyi.web.core.base.BaseController;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: wtao
 * @Date: 2018-12-23 17:22
 * @Version 1.0
 */
@Controller
@RequestMapping("/system/templet")
public class SysTempletController extends BaseController {

    @Autowired
    private ISysTempletService dataService;

    private String prefix = "system/templet";

    @RequiresPermissions("system:templet:view")
    @GetMapping()
    public String templet() {
        return prefix + "/templet";
    }

    @RequiresPermissions("system:templet:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(SysTemplet data) {
        startPage();
        List<SysTemplet> list = dataService.selectTempletList(data);
        return getDataTable(list);
    }

    @RequiresPermissions("system:templet:remove")
    @Log(title = "数据管理", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        try {
            return toAjax(dataService.deleteTempletByIds(ids));
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    @GetMapping("/add")
    public String add() {
        return prefix + "/add";
    }

    @RequiresPermissions("system:templet:add")
    @Log(title = "数据管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @Transactional(rollbackFor = Exception.class)
    @ResponseBody
    public AjaxResult addSave(SysTemplet data) {
        data.setCreateBy(ShiroUtils.getLoginName());
        ShiroUtils.clearCachedAuthorizationInfo();
        return toAjax(dataService.insertTempletData(data));

    }

    @GetMapping("/edit/{roleId}")
    public String edit(@PathVariable("roleId") Long roleId, ModelMap mmap) {
//        mmap.put("role", roleService.selectRoleById(roleId));
        mmap.put("id", roleId);
        return prefix + "/edit";
    }

    @RequiresPermissions("system:templet:edit")
    @Log(title = "数据管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @Transactional(rollbackFor = Exception.class)
    @ResponseBody
    public AjaxResult editSave(SysTemplet templet) {
        templet.setUpdateBy(ShiroUtils.getLoginName());
        ShiroUtils.clearCachedAuthorizationInfo();
        return toAjax(dataService.updateTemplet(templet));
    }
}
