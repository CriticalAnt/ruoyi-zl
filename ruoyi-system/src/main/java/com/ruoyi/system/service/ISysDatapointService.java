package com.ruoyi.system.service;

import com.ruoyi.system.domain.SysDatapoint;

import java.util.List;

/**
 * @Author: wtao
 * @Date: 2018-12-25 21:15
 * @Version 1.0
 */
public interface ISysDatapointService {

    int insert(SysDatapoint datapoint);

    int countByTempId(int tempId);

    List<SysDatapoint> findAll();

    int deleteByTempId(long tempId);

    int deleteDatapointByIds(String ids);

    SysDatapoint selectById(Long dicId);

    List<SysDatapoint> selectByTempId(Long tempId);

    int update(SysDatapoint datapoint);

    List<SysDatapoint> selectByIds(String ids);
}
