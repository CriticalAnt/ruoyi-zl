package com.ruoyi.system.service.impl;

import com.ruoyi.common.support.Convert;
import com.ruoyi.system.domain.SysDevice;
import com.ruoyi.system.mapper.SysDeviceMapper;
import com.ruoyi.system.service.ISysDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: wtao
 * @Date: 2018-12-26 16:54
 * @Version 1.0
 */
@Service
public class SysDeviceServiceImpl implements ISysDeviceService {

    @Autowired
    SysDeviceMapper deviceMapper;

    @Override
    public List<SysDevice> findAll() {
        return deviceMapper.findAll();
    }

    @Override
    public int deleteDeviceByIds(String ids) {
        Long[] devId = Convert.toLongArray(ids);
        return deviceMapper.deleteDeviceByIds(devId);
    }
}
