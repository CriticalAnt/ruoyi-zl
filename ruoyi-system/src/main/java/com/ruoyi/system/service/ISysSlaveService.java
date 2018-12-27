package com.ruoyi.system.service;

import com.ruoyi.system.domain.SysDatapoint;
import com.ruoyi.system.domain.SysSlave;

import java.util.List;

/**
 * @Author: wtao
 * @Date: 2018-12-28 2:44
 * @Version 1.0
 */
public interface ISysSlaveService {

    int insert(SysSlave slave);

    List<SysSlave> selectByDevId(Long devId);
}
