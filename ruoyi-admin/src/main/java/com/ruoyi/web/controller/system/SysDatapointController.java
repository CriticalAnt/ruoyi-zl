package com.ruoyi.web.controller.system;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.base.AjaxResult;
import com.ruoyi.system.domain.SysDatapoint;
import com.ruoyi.system.service.ISysDatapointService;
import com.ruoyi.web.core.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: wtao
 * @Date: 2018-12-24 14:46
 * @Version 1.0
 */
@Controller
@RequestMapping("system/datapoint")
public class SysDatapointController extends BaseController {

    @Autowired
    private ISysDatapointService datapointService;

    private String prefix = "system/datapoint";

    @GetMapping("/add/{tempId}")
    public String add(@PathVariable("tempId") Long tempId, ModelMap mmap) {
        mmap.put("tempId", tempId);
        return prefix + "/add";
    }

    @ResponseBody
    @PostMapping("/add")
    public AjaxResult addDatapoint(@RequestParam("list") String pointJson) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JavaType jt = mapper.getTypeFactory().constructParametricType(ArrayList.class, SysDatapoint.class);
        List<SysDatapoint> points = mapper.readValue(pointJson, jt);
        int num = 0;
        for (SysDatapoint point : points) {
            num += datapointService.insert(point);
        }
        return toAjax(num == points.size() ? 1 : 0);
//        return "system/templet/templet";
    }
}
