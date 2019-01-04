package com.ruoyi.system.service.impl;

import com.ruoyi.common.support.Convert;
import com.ruoyi.system.domain.SysDevice;
import com.ruoyi.system.mapper.SysDeviceMapper;
import com.ruoyi.system.mapper.SysSlaveMapper;
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

    @Autowired
    SysSlaveMapper slaveMapper;

    @Override
    public List<SysDevice> findAll() {
        return deviceMapper.findAll();
    }

    @Override
    public int deleteDeviceByIds(String ids) {
        Long[] devIds = Convert.toLongArray(ids);
        slaveMapper.deleteByDevIds(devIds);
        return deviceMapper.deleteDeviceByIds(devIds);
    }

    @Override
    public SysDevice selectById(Long id) {
        return deviceMapper.selectById(id);
    }

    @Override
    public List<SysDevice> selectByIds(Long[] ids) {
        return deviceMapper.selectByIds(ids);
    }

    @Override
    public int insert(SysDevice device) {
        return deviceMapper.insert(device);
    }

    @Override
    public int update(SysDevice device) {
        return deviceMapper.update(device);
    }
}
