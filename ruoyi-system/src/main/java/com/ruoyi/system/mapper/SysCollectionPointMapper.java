package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysCollectionPoint;

import java.util.List;

/**
 * @Author: wtao
 * @Date: 2018-12-30 2:23
 * @Version 1.0
 */
public interface SysCollectionPointMapper {

    List<SysCollectionPoint> findAll();

    List<SysCollectionPoint> selectByDevId(int devId);
}
