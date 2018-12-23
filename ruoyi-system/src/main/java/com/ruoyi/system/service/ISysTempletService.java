package com.ruoyi.system.service;

import com.ruoyi.system.domain.SysTemplet;

import java.util.List;

/**
 * @Author: wtao
 * @Date: 2018-12-23 17:55
 * @Version 1.0
 */
public interface ISysTempletService {

    List<SysTemplet> selectTempletList(SysTemplet data);

    int deleteTempletByIds(String ids);

    int insertTempletData(SysTemplet data);
}
