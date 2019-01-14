package com.ruoyi.system.service;

import com.ruoyi.common.base.AjaxResult;
import com.ruoyi.system.domain.SysCollectionPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author: wtao
 * @Date: 2019-01-05 17:28
 * @Version 1.0
 */
public interface ISysDataService {
    AjaxResult export(HttpServletRequest request, HttpServletResponse response, List<SysCollectionPoint> points);
}
