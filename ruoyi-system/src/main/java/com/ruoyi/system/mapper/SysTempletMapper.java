package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysTemplet;

import java.util.List;

/**
 * @Author: wtao
 * @Date: 2018-12-23 18:01
 * @Version 1.0
 */
public interface SysTempletMapper {

    List<SysTemplet> selectTempletList(SysTemplet data);

    int deleteTempletByIds(Long[] ids);

    int insertTemplet(SysTemplet data);
}
