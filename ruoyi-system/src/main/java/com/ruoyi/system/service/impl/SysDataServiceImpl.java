package com.ruoyi.system.service.impl;

import com.ruoyi.common.base.AjaxResult;
import com.ruoyi.common.config.Global;
import com.ruoyi.system.domain.SysCollectionPoint;
import com.ruoyi.system.service.ISysDataService;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: wtao
 * @Date: 2019-01-05 17:28
 * @Version 1.0
 */
@Service
public class SysDataServiceImpl implements ISysDataService {
    @Override
    public AjaxResult export(HttpServletRequest request, HttpServletResponse response, List<SysCollectionPoint> points) {
        Map<String, List<SysCollectionPoint>> results = new HashMap<>();
        List<Date> dates = new ArrayList<>();
        List<String> strKeys = new ArrayList<>();
        List<String> keys = new ArrayList<>();
        OutputStream out = null;
        HSSFWorkbook workbook = null;
        try {

            for (SysCollectionPoint point : points) {
                String key = point.getSlaveId() + "-" + point.getPointId();
                String strKey = point.getSlaveName() + ":" + point.getPointName();
                if (!results.containsKey(key)) {
                    results.put(key, new ArrayList<>());
                    strKeys.add(strKey);
                    keys.add(key);
                }
                results.get(key).add(point);
                Date date = new Date(point.getUpdateTime().getTime() / (60 * 1000) * (60 * 1000));
                point.setUpdateTime(date);
                if (!dates.contains(date))
                    dates.add(date);
            }
            // 产生工作薄对象
            workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet();
            workbook.setSheetName(0, "历史数据");
            HSSFRow row;
            HSSFCell cell; // 产生单元格
            row = sheet.createRow(0);
            cell = row.createCell(0);
            cell.setCellValue("更新时间");
            //创建首行标题名称
            for (int i = 1; i <= strKeys.size(); i++) {
                cell = row.createCell(i);
                cell.setCellValue(strKeys.get(i - 1));
            }
            Collections.sort(dates);
            //创建所有更新时间
            for (int index = 1; index <= dates.size(); index++) {
                row = sheet.createRow(index);
                cell = row.createCell(0);
                cell.setCellValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .format(dates.get(index - 1)));
            }
            //填入数据
            for (Map.Entry<String, List<SysCollectionPoint>> entry : results.entrySet()) {
                List<SysCollectionPoint> pointList = entry.getValue();
                String key = entry.getKey();
                int indexCol = keys.indexOf(key);
                for (SysCollectionPoint point : pointList) {
                    row = sheet.getRow(dates.indexOf(point.getUpdateTime()) + 1);
                    cell = row.createCell(indexCol + 1);
                    cell.setCellValue(point.getValue() + point.getUnit());
                }
            }
            String filename = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
            filename = encodingFilename(filename);
            out = new FileOutputStream(getAbsoluteFile(filename));
            workbook.write(out);
            return AjaxResult.success(filename);
//            out = response.getOutputStream();
//            response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes("gb2312"), "ISO8859-1") + ".xls");
//            workbook.write(out);
//            return AjaxResult.success(filename);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("导出Excel失败，请联系网站管理员！");
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 编码文件名
     */
    public String encodingFilename(String filename) {
        filename = UUID.randomUUID().toString() + "_" + filename + ".xls";
        return filename;
    }

    /**
     * 获取下载路径
     *
     * @param filename 文件名称
     */
    public String getAbsoluteFile(String filename) {
        String downloadPath = Global.getDownloadPath() + filename;
        File desc = new File(downloadPath);
        if (!desc.getParentFile().exists()) {
            desc.getParentFile().mkdirs();
        }
        return downloadPath;
    }
}
