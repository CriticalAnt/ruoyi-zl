package com.ruoyi.server;

import com.ruoyi.server.common.Constant2;
import com.ruoyi.server.utils.UtilsCRC;
import com.ruoyi.system.domain.SysCollectionPoint;
import io.netty.channel.ChannelHandlerContext;
import org.nfunk.jep.JEP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: wtao
 * @Date: 2019-01-01 16:38
 * @Version 1.0
 */

@Service
public class ReceiveService {

    @Autowired
    MongoTemplate mongoTemplate;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public void receive(byte[] req, ChannelHandlerContext ctx) {
        int index;
        List<SysCollectionPoint> points;
        SysCollectionPoint point;
        synchronized (Constant2.ctxIndex) {
            index = Constant2.ctxIndex.get(ctx);
        }
        synchronized (Constant2.ctxPoints) {
            points = Constant2.ctxPoints.get(ctx);
        }
        //当前处理的数据点
        point = points.get(index);
        if (req.length < 3)
            return;
        //数据解析
        int equNum = req[0] & 0xFF;
        int funCode = req[1] & 0xFF;
        int len = req[2] & 0xFF;
        byte[] data = new byte[len];
        int valueType = point.getValueType();
        //校验
        if (equNum != point.getEquNum()) {
            return;
        }
        if (funCode != 3 && funCode != 4) {
            return;
        }
        if (valueType == 0 || valueType == 1) {
            if (len != 2) {
                return;
            }
        } else {
            if (len != 4) {
                return;
            }
        }
        //CRC
        if (!isCRC(req))
            return;
        try {
            System.arraycopy(req, 3, data, 0, len);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        for (byte b : req) {
            System.out.print(String.valueOf(b & 0xFF) + " ");
        }
        System.out.println();
        //数据解析、运算
        long v;
        float f;
        double temp;
        String formula = point.getFormula();
        JEP jep = new JEP();
        if (valueType == 6 || valueType == 7) {
            f = getFloat(data, valueType, point.getDecimalLen());
            jep.addVariable("x", f);
        } else {
            v = getInteger(data, point.getValueType());
            jep.addVariable("x", v);
        }
        jep.parseExpression(formula);
        temp = jep.getValue();
        //小数位数和单位
        int decimalLen = point.getDecimalLen();
        DecimalFormat df;
        if (decimalLen != 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < decimalLen; i++) {
                sb.append("0");
            }
            String format = sb.toString();
            df = new DecimalFormat("#." + format);
        } else {
            df = new DecimalFormat("#");
        }
        String res = df.format(temp);
        point.setValue(res);
        point.setUpdateTime(new Date());
        point.setId(null);
        points.set(index, point);
        res += point.getUnit();
        synchronized (Constant2.ctxPoints) {
            Constant2.ctxPoints.put(ctx, points);
        }
        executorService.execute(() -> mongoTemplate.insert(point));
        System.out.println("result: " + res);
    }

    private float getFloat(byte[] data, int valueType, int decimalLen) {
        float value = 0.0f;
        switch (valueType) {
            //4字节浮点型(AB CD)
            case (6):
                int l = (data[0]) << 24;
                l |= (data[1] & 0xFF) << 16;
                l |= (data[2] & 0xFF) << 8;
                l |= (data[3] & 0xFF);
                value = Float.intBitsToFloat(l);
                break;
            //4字节浮点型(CD AB)
            case (7):
                int i = (data[0] & 0xFF);
                i |= (data[1] & 0xFF) << 8;
                i |= (data[2] & 0xFF) << 16;
                i |= (data[3]) << 24;
                value = Float.intBitsToFloat(i);
                break;
        }
        return value;
    }

    private long getInteger(byte[] data, int valueType) {
        long value = 0;
        switch (valueType) {
            //2字节无符号整数
            case 0:
                value = (data[1] & 0xFF) | ((data[0] & 0xFF) << 8);
                break;
            //2字节有符号整数
            case 1:
                value = (data[1] & 0xFF) | (data[0] << 8);
                break;
            //4字节无符号整数(AB CD)
            case 2:
                value = (data[0] & 0xFF) << 24;
                value |= (data[1] & 0xFF) << 16;
                value |= (data[2] & 0xFF) << 8;
                value |= (data[3] & 0xFF);
                break;
            //4字节无符号整数(CD AB)
            case 3:
                value = (data[0] & 0xFF);
                value |= (data[1] & 0xFF) << 8;
                value |= (data[2] & 0xFF) << 16;
                value |= (data[3] & 0xFF) << 24;
                break;
            //4字节有符号整数(AB CD)
            case 4:
                value = (data[0]) << 24;
                value |= (data[1] & 0xFF) << 16;
                value |= (data[2] & 0xFF) << 8;
                value |= (data[3] & 0xFF);
                break;
            //4字节有符号整数(CD AB)
            case 5:
                value = (data[0] & 0xFF);
                value |= (data[1] & 0xFF) << 8;
                value |= (data[2] & 0xFF) << 16;
                value |= (data[3]) << 24;
                break;
        }
        return value;
    }

    private boolean isCRC(byte[] req) {
        byte[] temp = new byte[req.length - 2];
        System.arraycopy(req, 0, temp, 0, temp.length);
        int crc = UtilsCRC.getCRC(temp);
        if (req[req.length - 2] == (byte) (crc & 0xFF)
                && req[req.length - 1] == (byte) ((crc >> 8) & 0xFF)) {
            return true;
        }
        return false;
    }
}
