package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysSlave;

import java.util.List;

/**
 * @Author: wtao
 * @Date: 2018-12-28 2:45
 * @Version 1.0
 */
public interface SysSlaveMapper {

    int insert(SysSlave slave);

    List<SysSlave> selectByDevId(Long devId);

    int deleteByIds(Long[] slaveIds);

    SysSlave selectById(Long id);

    int update(SysSlave slave);

    int deleteByDevIds(Long[] devIds);

    List<SysSlave> findAll();

    List<SysSlave> selectByIds(Long[] slaveIds);
}
