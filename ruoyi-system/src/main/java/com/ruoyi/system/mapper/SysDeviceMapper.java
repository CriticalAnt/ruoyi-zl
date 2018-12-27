package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysDevice;

import java.util.List;

/**
 * @Author: wtao
 * @Date: 2018-12-26 16:56
 * @Version 1.0
 */
public interface SysDeviceMapper {

    List<SysDevice> findAll();

    int deleteDeviceByIds(Long[] devId);
}
