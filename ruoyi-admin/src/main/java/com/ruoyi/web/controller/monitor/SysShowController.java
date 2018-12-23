package com.ruoyi.web.controller.monitor;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: wtao
 * @Date: 2018-12-23 16:59
 * @Version 1.0
 */

@Controller
@RequestMapping("/monitor/show")
public class SysShowController {

    private String prefix = "monitor/show";

    @RequiresPermissions("monitor:show:view")
    @GetMapping()
    public String role() {
        return prefix + "/list";
    }
}
