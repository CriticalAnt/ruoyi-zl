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
import com.ruoyi.server.common.Constant2;
import com.ruoyi.system.domain.SysCollectionPoint;
import com.ruoyi.system.domain.SysDevice;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.mapper.SysCollectionPointMapper;
import com.ruoyi.system.service.ISysDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * web层通用数据处理
 *
 * @author ruoyi
 */
public class BaseController {

    @Autowired
    ISysDeviceService deviceService;

    @Autowired
    SysCollectionPointMapper pointMapper;

    /**
     * 更新TCP服务中的连接内容
     */
    public void updatePoints() {
        List<SysDevice> devices = deviceService.findAll();
        List<SysCollectionPoint> points = pointMapper.findAll();
        for (SysDevice device : devices) {
            List<SysCollectionPoint> list = new ArrayList<>();
            for (SysCollectionPoint point : points) {
                if (point.getDevId() == device.getId()) {
                    list.add(point);
                }
            }
            Constant2.codePoint.put(device.getCode(), list);
            Constant2.ctxPoints.put(Constant2.registeredCtx.get(device.getCode())
                    , list);
        }
    }

    /**
     * 更新设备信息
     */
    public void updateDevice(String ids) {
        List<SysDevice> devices = deviceService.selectByIds(Convert.toLongArray(ids));
        for (SysDevice device : devices) {
            String code = device.getCode();
            Constant2.registeredCtx.get(code).close();
            Constant2.ctxPoints.remove(Constant2.registeredCtx.get(code));
            Constant2.registeredCode.remove(code);
            Constant2.registeredCtx.remove(code);
            Constant2.codePoint.remove(code);
        }
    }

    public void addDevice(SysDevice device) {
        String code = device.getCode();
        Constant2.registeredCode.put(code, "0");
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
