package com.ruoyi.web.controller.system;

import com.ruoyi.system.domain.SysCollectionPoint;
import com.ruoyi.system.domain.SysDevice;
import com.ruoyi.system.domain.SysSlave;
import com.ruoyi.system.mapper.SysCollectionPointMapper;
import com.ruoyi.system.service.ISysDatapointService;
import com.ruoyi.system.service.ISysDeviceService;
import com.ruoyi.system.service.ISysSlaveService;
import com.ruoyi.web.core.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: wtao
 * @Date: 2019-01-02 11:53
 * @Version 1.0
 */
@Controller
@RequestMapping("system/data")
public class SysDataController extends BaseController {

    private String prefix = "system/data";

    @Autowired
    ISysDeviceService deviceService;
    @Autowired
    ISysSlaveService slaveService;
    @Autowired
    ISysDatapointService datapointService;
    @Autowired
    SysCollectionPointMapper collectionPointMapper;
    @Autowired
    MongoTemplate mongoTemplate;

    @GetMapping
    public String data(ModelMap modelMap) {
        List<SysDevice> devices = deviceService.findAll();
        List<SysSlave> slaves = slaveService.findAll();
        List<SysCollectionPoint> points = collectionPointMapper.findAll();
        modelMap.put("devices", devices);
        modelMap.put("slaves", slaves);
        modelMap.put("points", points);
        return prefix + "/data";
    }

    @GetMapping("list")
    @ResponseBody
    public Map<String, Object> list(Long devId, Long slaveId, Long pointId, String startTime, String endTime) throws ParseException {
        Map<String, Object> map = new HashMap<>();
        if (devId == null || slaveId == null) {
            map.put("msg", "设备ID或从机ID不能为空");
            return map;
        }
        if (devId < 0 || slaveId < 0) {
            map.put("msg", "设备ID或从机ID不能为空");
            return map;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
        Date startDate = startTime.equals("") ? null : dateFormat.parse(startTime);
        Date endDate = endTime.equals("") ? null : dateFormat.parse(endTime);
        //默认一天
        if (startDate == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, -1);
            startDate = dateFormat.parse(dateFormat.format(calendar.getTime()));
        }
        if (endDate == null) {
            endDate = dateFormat.parse(dateFormat.format(new Date()));
        }
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (pointId != null && pointId > 0) {
            criteria.and("pointId").is(pointId);
        }
        criteria.and("devId").is(devId).and("slaveId").is(slaveId);
        criteria.and("updateTime").gte(startDate).lte(endDate);
        query.addCriteria(criteria);
        List<SysCollectionPoint> points = mongoTemplate.find(query, SysCollectionPoint.class);
        Map<Integer, List<SysCollectionPoint>> results = new HashMap<>();
        for (SysCollectionPoint point : points) {
            if (!results.containsKey(point.getPointId()))
                results.put(point.getPointId(), new ArrayList<>());
            results.get(point.getPointId()).add(point);
        }
        List<Long> xdata = new ArrayList<>();
        List<Map<String, Object>> series = new ArrayList<>();
        Long sub = (endDate.getTime() - startDate.getTime()) / 10;
        Long start = startDate.getTime();
        Long min = Long.MAX_VALUE;
        Long max = Long.MIN_VALUE;
        for (int i = 0; i < 10; i++) {
            xdata.add(start + sub * i);
        }
        for (Map.Entry<Integer, List<SysCollectionPoint>> entry : results.entrySet()) {
            Map<String, Object> resMap = new HashMap<>();
            List<Object[]> data = new ArrayList<>();
            SysCollectionPoint p = entry.getValue().get(0);
            for (SysCollectionPoint point : entry.getValue()) {
                Long l = point.getUpdateTime().getTime();
                min = min > l ? l : min;
                max = max < l ? l : max;
                String value = point.getValue();
//                if (value != null && value.length() > 1) {
//                    value = value.substring(0, value.length() - 1);
                data.add(new Object[]{l, value});
//                }
            }
            resMap.put("name", p.getPointName());
            resMap.put("type", "line");
            resMap.put("data", data);
            series.add(resMap);
        }
        map.put("xdata", xdata);
        map.put("series", series);
        map.put("min", min);
        map.put("max", max);
        return map;
    }
}
