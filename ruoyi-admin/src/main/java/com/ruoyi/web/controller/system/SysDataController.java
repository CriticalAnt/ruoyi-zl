package com.ruoyi.web.controller.system;

import com.ruoyi.common.base.AjaxResult;
import com.ruoyi.system.domain.SysCollectionPoint;
import com.ruoyi.system.domain.SysDevice;
import com.ruoyi.system.domain.SysSlave;
import com.ruoyi.system.mapper.SysCollectionPointMapper;
import com.ruoyi.system.service.ISysDataService;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    ISysDataService dataService;
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
    public Map<String, Object> list(Long devId, String slaveIds, String pointIds, String startTime, String endTime) throws ParseException {
        Map<String, Object> map = new HashMap<>();
        String[] Ids = pointIds.split(",");
        Map<Long, List<Long>> mapIds = new HashMap<>();
        for (String id : Ids) {
            Long sId = Long.valueOf(id.split("_")[0]);
            Long pId = Long.valueOf(id.split("_")[1]);
            if (mapIds.containsKey(sId)) {
                mapIds.get(sId).add(pId);
            } else {
                mapIds.put(sId, new ArrayList<Long>() {{
                    add(pId);
                }});
            }
        }
        if (devId == null || mapIds.size() == 0) {
            map.put("msg", "设备ID或从机ID不能为空");
            return map;
        }
        if (devId < 0) {
            map.put("msg", "设备ID或从机ID不能为空");
            return map;
        }
        List<SysCollectionPoint> points = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
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
        long star = System.currentTimeMillis();
        for (Map.Entry<Long, List<Long>> entry : mapIds.entrySet()) {
            Long[] pIds = new Long[entry.getValue().size()];
            entry.getValue().toArray(pIds);
            Long sId = entry.getKey();
            Query query = new Query();
            Criteria criteria = new Criteria();
            if (pIds.length > 0)
                criteria.and("pointId").in(pIds);
            criteria.and("devId").is(devId).and("slaveId").is(sId);
            criteria.and("updateTime").gte(startDate).lte(endDate);
            query.addCriteria(criteria);
            points.addAll(mongoTemplate.find(query, SysCollectionPoint.class));
        }

        long end = System.currentTimeMillis();
        System.out.println("查询耗时：" + (end - star) + "ms");
        Map<String, List<SysCollectionPoint>> results = new HashMap<>();
        List<String> strKeys = new ArrayList<>();
        List<String> keys = new ArrayList<>();
        for (SysCollectionPoint point : points) {
            String key = point.getSlaveId() + "-" + point.getPointId();
            String strKey = point.getSlaveName() + ":" + point.getPointName();
            if (!results.containsKey(key)) {
                results.put(key, new ArrayList<>());
                strKeys.add(strKey);
                keys.add(key);
            }
            results.get(key).add(point);
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
        for (Map.Entry<String, List<SysCollectionPoint>> entry : results.entrySet()) {
            Map<String, Object> resMap = new HashMap<>();
            List<Object[]> data = new ArrayList<>();
            String name = "";
            for (SysCollectionPoint point : entry.getValue()) {
                if (name.equals("") || !name.equals(point.getSlaveName() + ":" + point.getPointName()))
                    name = point.getSlaveName() + ":" + point.getPointName();
                Long l = point.getUpdateTime().getTime();
                min = min > l ? l : min;
                max = max < l ? l : max;
                String value = point.getValue();
                data.add(new Object[]{l, value});
            }
            resMap.put("name", name);
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

    @GetMapping("/export")
    @ResponseBody
    public AjaxResult export(HttpServletRequest request, HttpServletResponse response,
                             Long devId, String slaveIds, String pointIds, String startTime, String endTime) throws ParseException {

        Map<String, Object> map = new HashMap<>();
        String[] Ids = pointIds.split(",");
        Map<Long, List<Long>> mapIds = new HashMap<>();
        for (String id : Ids) {
            Long sId = Long.valueOf(id.split("_")[0]);
            Long pId = Long.valueOf(id.split("_")[1]);
            if (mapIds.containsKey(sId)) {
                mapIds.get(sId).add(pId);
            } else {
                mapIds.put(sId, new ArrayList<Long>() {{
                    add(pId);
                }});
            }
        }
        if (devId == null || mapIds.size() == 0) {
            map.put("msg", "设备ID或从机ID不能为空");
            return AjaxResult.error("导出Excel失败，设备ID或从机ID不能为空！");
        }
        if (devId < 0) {
            map.put("msg", "设备ID或从机ID不能为空");
            return AjaxResult.error("导出Excel失败，设备ID或从机ID不能为空！");
        }
        List<SysCollectionPoint> points = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
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
        for (Map.Entry<Long, List<Long>> entry : mapIds.entrySet()) {
            Query query = new Query();
            Criteria criteria = new Criteria();
            Long[] pIds = new Long[entry.getValue().size()];
            entry.getValue().toArray(pIds);
            Long sId = entry.getKey();
            if (pIds.length > 0)
                criteria.and("pointId").in(pIds);
            criteria.and("devId").is(devId).and("slaveId").is(sId);
            criteria.and("updateTime").gte(startDate).lte(endDate);
            query.addCriteria(criteria);
            points.addAll(mongoTemplate.find(query, SysCollectionPoint.class));
        }
        return dataService.export(request, response, points);
    }
}
