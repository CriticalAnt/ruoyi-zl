package com.ruoyi.web.controller.system;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: wtao
 * @Date: 2018-12-24 14:46
 * @Version 1.0
 */
@Controller
@RequestMapping("system/datapoint")
public class SysDatapointController {


    private String prefix = "system/datapoint";

    @GetMapping("/add")
    public String add() {
        return prefix + "/add";
    }
}
