package com.ruoyi.web.core.base;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.base.AjaxResult;
import com.ruoyi.common.support.Convert;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.framework.web.page.PageDomain;
import com.ruoyi.framework.web.page.TableDataInfo;
import com.ruoyi.framework.web.page.TableSupport;
import com.ruoyi.quartz.QuartzManager;
import com.ruoyi.server.common.ConstantState;
import com.ruoyi.server.domain.ResolveRecord;
import com.ruoyi.system.domain.SysCollectionPoint;
import com.ruoyi.system.domain.SysDevice;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.mapper.SysCollectionPointMapper;
import com.ruoyi.system.service.ISysDeviceService;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * web层通用数据处理
 *
 * @author ruoyi
 */
public class BaseController {

    private static final Logger log = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    ISysDeviceService deviceService;

    @Autowired
    SysCollectionPointMapper pointMapper;

    @Autowired
    QuartzManager quartzManager;

    /**
     * 更新TCP服务中的连接内容
     */
    public void updatePointsByTempId(Set<Integer> set) {
        List<SysDevice> devices = deviceService.findAll();
        List<SysCollectionPoint> points = pointMapper.findAll();
        Collections.sort(points, (o1, o2) -> {
            int x = Integer.valueOf(o1.getRegisterAdr())
                    - Integer.valueOf(o2.getRegisterAdr());
            return x;

        });
        for (SysDevice device : devices) {
            String rgsCode = device.getCode();
            Map<String, ResolveRecord> map = new HashMap<>();
            for (SysCollectionPoint point : points) {
                if (point.getDevId() == device.getId()) {
                    int equNum = point.getEquNum();
                    int code = Integer.valueOf(point.getRegisterAdr()) / 10000;
                    code = code == 3 ? 4 : 3;
                    String key = equNum + "-" + code;
                    if (!map.containsKey(key)) {
                        ResolveRecord record = new ResolveRecord(point);
                        map.put(key, record);
                    } else
                        map.get(key).getPoints().add(point);
                }
            }
            ConstantState.codeRecord.put(rgsCode, map);
            ConstantState.codeCron.put(device.getCode(), device.getFrequency());
            if (ConstantState.registeredCtx.get(rgsCode) != null) {
                ConstantState.ctxRecord.remove(ConstantState.registeredCtx.get(rgsCode));
                ConstantState.registeredCtx.get(rgsCode).close();
                ConstantState.registeredCtx.remove(rgsCode);
                ConstantState.registeredCode.put(rgsCode, "0");
            }
        }
    }

    /**
     * 更新TCP服务中的连接内容
     */
    public void updatePointsByDevId(Set<Integer> set) {
        List<SysDevice> devices = deviceService.findAll();
        List<SysCollectionPoint> points = pointMapper.findAll();
        Collections.sort(points, (o1, o2) -> {
            int x = Integer.valueOf(o1.getRegisterAdr())
                    - Integer.valueOf(o2.getRegisterAdr());
            return x;

        });
        for (SysDevice device : devices) {
            String rgsCode = device.getCode();
            Map<String, ResolveRecord> map = new HashMap<>();
            for (SysCollectionPoint point : points) {
                if (point.getDevId() == device.getId()) {
                    int equNum = point.getEquNum();
                    int code = Integer.valueOf(point.getRegisterAdr()) / 10000;
                    code = code == 3 ? 4 : 3;
                    String key = equNum + "-" + code;
                    if (!map.containsKey(key)) {
                        ResolveRecord record = new ResolveRecord(point);
                        map.put(key, record);
                    } else
                        map.get(key).getPoints().add(point);
                }
            }
            ConstantState.codeRecord.put(rgsCode, map);
            ConstantState.codeCron.put(device.getCode(), device.getFrequency());
            if (ConstantState.registeredCtx.get(rgsCode) != null) {
                ConstantState.ctxRecord.remove(ConstantState.registeredCtx.get(rgsCode));
                ConstantState.registeredCtx.get(rgsCode).close();
                ConstantState.registeredCtx.remove(rgsCode);
                ConstantState.registeredCode.put(rgsCode, "0");
            }
        }
    }

    /**
     * 更新设备信息
     */
    public void removeDevice(String ids) {
        List<SysDevice> devices = deviceService.selectByIds(Convert.toLongArray(ids));
        for (SysDevice device : devices) {
            String code = device.getCode();
            ConstantState.ctxRecord.remove(ConstantState.registeredCtx.get(code));
            if (ConstantState.registeredCtx.get(code) != null)
                ConstantState.registeredCtx.get(code).close();
            ConstantState.registeredCode.remove(code);
            ConstantState.registeredCtx.remove(code);
            ConstantState.codeRecord.remove(code);
            ConstantState.codeCron.remove(code);
        }
    }

    public void updateDevice(SysDevice device) throws Exception {
        String rgsCode = device.getCode();
        ChannelHandlerContext ctx = ConstantState.registeredCtx.get(rgsCode);
        ConstantState.codeName.put(device.getCode(), device.getDevName());
        if (ctx != null) {
            quartzManager.updateJob(ctx.channel().remoteAddress().toString(), rgsCode, device.getFrequency());
        }
    }

    public void addDevice(SysDevice device) {
        String code = device.getCode();
        ConstantState.registeredCode.put(code, "0");
        ConstantState.codeCron.put(code, device.getFrequency());
    }


    /**
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    /**
     * 设置请求分页数据
     */
    protected void startPage() {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize)) {
            String orderBy = pageDomain.getOrderBy();
            PageHelper.startPage(pageNum, pageSize, orderBy);
        }
    }

    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected TableDataInfo getDataTable(List<?> list) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(0);
        rspData.setRows(list);
        PageInfo pageInfo = new PageInfo(list);
        rspData.setTotal(pageInfo.getTotal());
        return rspData;
    }

    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected TableDataInfo getDataTable(List<?> list, List<?> list2) {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(0);
        rspData.setRows(list);
        PageInfo pageInfo = new PageInfo(list2);
        rspData.setTotal(pageInfo.getTotal());
        return rspData;
    }

    /**
     * 响应返回结果
     *
     * @param rows 影响行数
     * @return 操作结果
     */
    protected AjaxResult toAjax(int rows) {
        return rows > 0 ? success() : error();
    }

    /**
     * 返回成功
     */
    public AjaxResult success() {
        return AjaxResult.success();
    }

    /**
     * 返回失败消息
     */
    public AjaxResult error() {
        return AjaxResult.error();
    }

    /**
     * 返回成功消息
     */
    public AjaxResult success(String message) {
        return AjaxResult.success(message);
    }

    /**
     * 返回失败消息
     */
    public AjaxResult error(String message) {
        return AjaxResult.error(message);
    }

    /**
     * 返回错误码消息
     */
    public AjaxResult error(int code, String message) {
        return AjaxResult.error(code, message);
    }

    /**
     * 页面跳转
     */
    public String redirect(String url) {
        return StringUtils.format("redirect:{}", url);
    }

    public SysUser getUser() {
        return ShiroUtils.getUser();
    }

    public void setUser(SysUser user) {
        ShiroUtils.setUser(user);
    }

    public Long getUserId() {
        return getUser().getUserId();
    }

    public String getLoginName() {
        return getUser().getLoginName();
    }
}
