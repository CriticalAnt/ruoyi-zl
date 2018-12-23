package com.ruoyi.system.service.impl;

import com.ruoyi.common.support.Convert;
import com.ruoyi.system.domain.SysTemplet;
import com.ruoyi.system.mapper.SysTempletMapper;
import com.ruoyi.system.service.ISysTempletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: wtao
 * @Date: 2018-12-23 18:07
 * @Version 1.0
 */
@Service
public class SysTempletServiceImpl implements ISysTempletService {

    @Autowired
    SysTempletMapper sysTempletMapper;

    public List<SysTemplet> selectTempletList(SysTemplet data) {
        return sysTempletMapper.selectTempletList(data);
    }

    @Override
    public int deleteTempletByIds(String ids) {
        Long[] modelIds = Convert.toLongArray(ids);
        return sysTempletMapper.deleteTempletByIds(modelIds);
    }

    @Override
    public int insertTempletData(SysTemplet data) {
        return sysTempletMapper.insertTemplet(data);
    }
}
