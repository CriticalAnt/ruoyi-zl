package com.ruoyi.system.service.impl;

import com.ruoyi.common.support.Convert;
import com.ruoyi.system.domain.SysDatapoint;
import com.ruoyi.system.mapper.SysDatapointMapper;
import com.ruoyi.system.service.ISysDatapointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: wtao
 * @Date: 2018-12-25 21:16
 * @Version 1.0
 */
@Service
public class SysDatapointServiceImpl implements ISysDatapointService {

    @Autowired
    SysDatapointMapper datapointMapper;

    @Override
    public int insert(SysDatapoint datapoint) {
        return datapointMapper.insert(datapoint);
    }

    @Override
    public int countByTempId(int tempId) {
        return datapointMapper.countByTempId(tempId);
    }

    @Override
    public List<SysDatapoint> findAll() {
        return datapointMapper.findAll();
//        List<Map<String, String>> maps = new ArrayList<>();
//        for (SysDatapoint point : list) {
//            Map<String, String> map = new HashMap<>();
//            map.put("id", String.valueOf(point.getId()));
//            map.put("tempId", String.valueOf(point.getTempId()));
//            map.put("pointName", point.getPointName());
//            map.put("dataType", DataType.getDescByCode(point.getDataType()));
//            map.put("registerAdr", point.getRegisterAdr());
//            map.put("valueType", ValueType.getDescByCode(point.getValueType()));
//            map.put("decimalLen", String.valueOf(point.getDecimalLen()));
//            map.put("readType", ReadType.getDescByCode(point.getReadType()));
//            map.put("registerLen", point.getRegisterLen());
//            map.put("unit", point.getUnit());
//            map.put("formula", point.getFormula());
//            map.put("storageType", StorageType.getDescByCode(point.getStorageType()));
//            maps.add(map);
//        }
//        return maps;
    }

    @Override
    public int deleteByTempId(long tempId) {
        return datapointMapper.deleteByTempId(tempId);
    }

    @Override
    public int deleteDatapointByIds(String ids) {
        Long[] pointId = Convert.toLongArray(ids);
        return datapointMapper.deleteDatapointByIds(pointId);
    }

    @Override
    public SysDatapoint selectById(Long dicId) {
        return datapointMapper.selectById(dicId);
    }

    @Override
    public List<SysDatapoint> selectByTempId(Long tempId) {
        return datapointMapper.selectByTempId(tempId);
    }

    @Override
    public int update(SysDatapoint datapoint) {
        return datapointMapper.update(datapoint);
    }

    @Override
    public List<SysDatapoint> selectByIds(String ids) {
        Long[] pointId = Convert.toLongArray(ids);
        return datapointMapper.selectByIds(pointId);
    }

}
