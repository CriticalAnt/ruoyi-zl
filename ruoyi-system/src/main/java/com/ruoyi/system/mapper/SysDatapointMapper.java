package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysDatapoint;

import java.util.List;

/**
 * @Author: wtao
 * @Date: 2018-12-25 21:15
 * @Version 1.0
 */
public interface SysDatapointMapper {

    int insert(SysDatapoint datapoint);

    int countByTempId(int tempId);

    List<SysDatapoint> findAll();

    int deleteByTempId(long tempId);

    int deleteDatapointByIds(Long[] ids);

    SysDatapoint selectById(Long dicId);

    int update(SysDatapoint datapoint);
}
