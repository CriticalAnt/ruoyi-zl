package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.SysSlave;
import com.ruoyi.system.mapper.SysSlaveMapper;
import com.ruoyi.system.service.ISysSlaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: wtao
 * @Date: 2018-12-28 2:44
 * @Version 1.0
 */
@Service
public class SysSlaveServiceImple implements ISysSlaveService {

    @Autowired
    SysSlaveMapper slaveMapper;

    @Override
    public int insert(SysSlave slave) {
        return slaveMapper.insert(slave);
    }

    @Override
    public List<SysSlave> selectByDevId(Long devId) {
        return slaveMapper.selectByDevId(devId);
    }
}
