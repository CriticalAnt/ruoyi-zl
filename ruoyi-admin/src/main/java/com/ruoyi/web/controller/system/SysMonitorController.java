package com.ruoyi.web.controller.system;

import com.google.common.collect.Sets;
import com.ruoyi.common.base.AjaxResult;
import com.ruoyi.framework.web.page.TableDataInfo;
import com.ruoyi.server.common.ConstantState;
import com.ruoyi.server.domain.ResolveRecord;
import com.ruoyi.server.utils.UtilsCRC;
import com.ruoyi.system.domain.SysCollectionPoint;
import com.ruoyi.system.domain.SysDevice;
import com.ruoyi.system.mapper.SysCollectionPointMapper;
import com.ruoyi.system.service.ISysDeviceService;
import com.ruoyi.web.core.base.BaseController;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: wtao
 * @Date: 2019-01-02 4:43
 * @Version 1.0
 */
@Controller
@RequestMapping("system/monitor")
public class SysMonitorController extends BaseController {

    private String prefix = "system/monitor";

    private static final Logger log = LoggerFactory.getLogger(SysMonitorController.class);

    @Autowired
    ISysDeviceService deviceService;

    @Autowired
    SysCollectionPointMapper pointMapper;

    //    @RequiresPermissions("system:monitor:view")
    @GetMapping()
    public String dic(ModelMap modelMap) {
        List<SysDevice> devs = deviceService.findAll();
        List<SysCollectionPoint> points = pointMapper.findAll();
//        List<SysCollectionPoint> res = new ArrayList<>();
        Set<SysCollectionPoint> resSet = Sets.newHashSet();
        Map<ChannelHandlerContext, Map<String, ResolveRecord>> ps = new HashMap<>();
        synchronized (ConstantState.ctxRecord) {
            for (Map.Entry<ChannelHandlerContext, Map<String, ResolveRecord>> entry : ConstantState.ctxRecord.entrySet())
                ps.put(entry.getKey(), entry.getValue());
        }
        for (SysCollectionPoint p : points) {
            resSet.add(p);
        }
        for (Map.Entry<ChannelHandlerContext, Map<String, ResolveRecord>> entry : ps.entrySet()) {
            for (Map.Entry<String, ResolveRecord> pEntry : entry.getValue().entrySet()) {
                for (SysCollectionPoint point : pEntry.getValue().getPoints()) {
                    for (SysCollectionPoint p : points) {
                        if (p.getPointId() == point.getPointId()
                                && p.getSlaveId() == point.getSlaveId()
                                && p.getDevId() == point.getDevId()) {
                            resSet.remove(p);
                            p = point;
                            resSet.add(p);
                        }
                    }
                }
            }
        }
        List<SysCollectionPoint> list = new ArrayList<>(resSet);
        Collections.sort(list, (o1, o2) -> {
            int x = o1.getDevId() - o2.getDevId();
            int y = o1.getSlaveId() - o2.getSlaveId();
            int z = Integer.valueOf(o1.getRegisterAdr())
                    - Integer.valueOf(o2.getRegisterAdr());
            if (x == 0) {
                if (y == 0) {
                    return z;
                }
                return y;
            }
            return x;
        });
        modelMap.put("devs", devs);
        modelMap.put("points", list);
        return prefix + "/monitor";
    }

    @GetMapping("edit/{info}")
    public String edit(@PathVariable("info") String info, ModelMap mmap) {
        String[] ids = info.split(",");
        Long devId = Long.valueOf(ids[1]);
        int equNum = Integer.valueOf(ids[2]);
        int pointId = Integer.valueOf(ids[3]);
        int valueType = Integer.valueOf(ids[4]);
        int address = Integer.valueOf(ids[5]);
        SysDevice device = deviceService.selectById(devId);
        mmap.put("device", device);
        mmap.put("equNum", equNum);
        mmap.put("pointId", pointId);
        mmap.put("valueType", valueType);
        mmap.put("address", address);
        return prefix + "/edit";
    }

    @PostMapping("edit")
    @ResponseBody
    public AjaxResult edit(String code, int equNum, int pointId, int address, int valueType, String value) {
        ChannelHandlerContext ctx;
        synchronized (ConstantState.registeredCtx) {
            ctx = ConstantState.registeredCtx.get(code);
        }
        if (ctx == null) {
            return AjaxResult.error("当前设备不在线，发送失败");
        }
        int adr = address % 10000 - 1;
        int val = Integer.valueOf(value);
        byte[] bytes = new byte[6];
        byte[] b = new byte[8];
        bytes[0] = (byte) equNum;
        bytes[1] = (byte) 0x06;
        bytes[2] = (byte) ((adr >> 8) & 0xFF);
        bytes[3] = (byte) (adr & 0xFF);
        if (valueType == 0) {
            if (val > 65535 || val < 0)
                return AjaxResult.error("请取0-65535之间的数值");
            bytes[4] = (byte) ((val >> 8) & 0xFF);
            bytes[5] = (byte) (val & 0xFF);
        } else if (valueType == 1) {
            if (val > 32767 || val < -32768)
                return AjaxResult.error("请取-32768-32767之间的数值");
            bytes[4] = (byte) (val >> 8);
            bytes[5] = (byte) (val & 0xFF);
        } else {
            return AjaxResult.error("不支持当前数值类型");
        }
        int crc = UtilsCRC.getCRC(bytes);
        b[6] = (byte) (crc & 0xFF);
        b[7] = (byte) ((crc >> 8) & 0xFF);
        System.arraycopy(bytes, 0, b, 0, 6);
        ByteBuf buf = ctx.alloc().buffer(b.length);
        buf.writeBytes(b);
        try {
            ctx.writeAndFlush(buf);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
        return AjaxResult.success("发送成功");
    }

    @PostMapping("list")
    @ResponseBody
    public TableDataInfo list() {
        startPage();
        List<Map<String, String>> mapList = new ArrayList<>();
        List<SysDevice> devices = deviceService.findAll();
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, String> entry : ConstantState.registeredCode.entrySet())
            map.put(entry.getKey(), entry.getValue());
        for (SysDevice device : devices) {
            Map<String, String> res = new HashMap<>();
            res.put("id", String.valueOf(device.getId()));
            res.put("devNum", device.getDevNum());
            res.put("devName", device.getDevName());
            if ("1".equals(map.get(device.getCode()))) {
                res.put("status", "1");
            } else {
                res.put("status", "0");
            }
            mapList.add(res);
        }
        return getDataTable(mapList, devices);
    }

    @PostMapping("list/{devId}")
    @ResponseBody
    public TableDataInfo list(@PathVariable int devId) {
        List<SysDevice> devs = deviceService.findAll();
        List<SysCollectionPoint> points = pointMapper.selectByDevId(devId);
//        List<SysCollectionPoint> res = new ArrayList<>();
        Set<SysCollectionPoint> resSet = Sets.newHashSet();
        Map<ChannelHandlerContext, Map<String, ResolveRecord>> ps = new HashMap<>();
        synchronized (ConstantState.ctxRecord) {
            for (Map.Entry<ChannelHandlerContext, Map<String, ResolveRecord>> entry : ConstantState.ctxRecord.entrySet())
                ps.put(entry.getKey(), entry.getValue());
        }
        for (SysCollectionPoint p : points) {
            resSet.add(p);
        }
        log.info("列表展示Count: " + resSet.size());
        for (Map.Entry<ChannelHandlerContext, Map<String, ResolveRecord>> entry : ps.entrySet()) {
            for (Map.Entry<String, ResolveRecord> pEntry : entry.getValue().entrySet()) {
                for (SysCollectionPoint point : pEntry.getValue().getPoints()) {
                    for (SysCollectionPoint p : points) {
                        if (p.getPointId() == point.getPointId()
                                && p.getSlaveId() == point.getSlaveId()
                                && p.getDevId() == point.getDevId()) {
                            resSet.remove(p);
                            p = point;
                            resSet.add(p);
                        }
                    }
                }
            }
        }
        startPage();
        List<SysCollectionPoint> list = new ArrayList<>(resSet);
        Collections.sort(list, (o1, o2) -> {
            int x = o1.getDevId() - o2.getDevId();
            int y = o1.getSlaveId() - o2.getSlaveId();
            int z = Integer.valueOf(o1.getRegisterAdr())
                    - Integer.valueOf(o2.getRegisterAdr());
            if (x == 0) {
                if (y == 0) {
                    return z;
                }
                return y;
            }
            return x;
        });
        List<Map<String, String>> result = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        for (SysCollectionPoint point : list) {
            Map<String, String> map = new HashMap<>();
            map.put("pointName", point.getPointName());
            map.put("slaveName", point.getSlaveName());
            if (point.getRegisterAdr().startsWith("4") &&
                    (point.getReadType() == 1 || point.getReadType() == 2)) {
                map.put("edit", "1," + point.getDevId() + "," + point.getEquNum()
                        + "," + point.getPointId() + "," + point.getValueType()
                        + "," + point.getRegisterAdr());
            } else {
                map.put("edit", "0");
            }
            if (point.getValue() == null) {
                map.put("value", "-");
            } else {
                map.put("value", point.getValue() + point.getUnit());
            }
            if (point.getUpdateTime() == null) {
                map.put("updateTime", "-");
            } else {
                map.put("updateTime", format.format(point.getUpdateTime()));
            }
            result.add(map);
        }
        return getDataTable(result);
    }
}
