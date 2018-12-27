package com.ruoyi.system.service;

import com.ruoyi.system.domain.SysDevice;

import java.util.List;

/**
 * @Author: wtao
 * @Date: 2018-12-26 16:54
 * @Version 1.0
 */
public interface ISysDeviceService {

    List<SysDevice> findAll();

    int deleteDeviceByIds(String ids);
}
