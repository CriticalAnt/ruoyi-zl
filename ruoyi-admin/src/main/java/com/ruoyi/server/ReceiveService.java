package com.ruoyi.server;

import com.ruoyi.server.common.ConstantState;
import com.ruoyi.server.domain.ResolveRecord;
import com.ruoyi.server.utils.UtilsCRC;
import com.ruoyi.system.domain.SysCollectionPoint;
import com.ruoyi.websocket.server.WebSocketServer;
import io.netty.channel.ChannelHandlerContext;
import org.nfunk.jep.JEP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: wtao
 * @Date: 2019-01-01 16:38
 * @Version 1.0
 */

@Service
public class ReceiveService {

    private static final Logger log = LoggerFactory.getLogger(ReceiveService.class);

    @Autowired
    MongoTemplate mongoTemplate;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public void receive2(byte[] req, ChannelHandlerContext ctx) {
        Map<String, ResolveRecord> records;
        synchronized (ConstantState.ctxRecord) {
            records = ConstantState.ctxRecord.get(ctx);
        }

        //数据解析
        int equNum = req[0] & 0xFF;
        int funCode = req[1] & 0xFF;
        int len = req[2] & 0xFF;
        byte[] datas = new byte[len];
        if (len + 5 != req.length) {
            log.error(218 + ":长度错误");
            return;
        }
        //校验
        if (funCode != 3 && funCode != 4) {
            log.error(223 + ":功能码错误");
            return;
        }
        //CRC
        if (!UtilsCRC.isCRC(req)) {
            log.error(228 + ":CRC错误");
            return;
        }
        try {
            System.arraycopy(req, 3, datas, 0, len);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        String key = equNum + "-" + funCode;
        ResolveRecord record = records.get(key);
        int reader = record.getReader();
        List<SysCollectionPoint> points = record.getPoints();
        int startAdr = record.getReaderAdr();
        try {
            for (int i = 0; i < points.size(); i++) {
                if (i < reader) {
                    continue;
                }
                SysCollectionPoint point = points.get(i);
                int adr = Integer.valueOf(point.getRegisterAdr()) % 10000 - 1;
                log.info("地址: " + adr);
                int valueType = point.getValueType();
                int length = valueType == 0 ? 1 : valueType == 1 ? 1 : 2;
                length *= 2;
                byte[] data = new byte[length];
                int index = (adr - startAdr) * 2;
                log.info("startAdr: " + startAdr);
                log.info("index: " + index);
                if (index >= len) {
                    record.setReaderAdr(startAdr + len / 2);
                    log.info("readerAdr:" + record.getReaderAdr());
                    log.info("超出返回数据长度");
                    break;
                }
                record.resetReaderAdr();
                System.arraycopy(datas, index, data, 0, length);
                long v;
                float f;
                double temp;
                String formula = point.getFormula();
                JEP jep = new JEP();
                if (valueType == 6 || valueType == 7) {
                    f = getFloat(data, valueType, point.getDecimalLen());
                    jep.addVariable("x", f);
                } else {
                    v = getInteger(data, valueType);
                    jep.addVariable("x", v);
                }
                jep.parseExpression(formula);
                temp = jep.getValue();
                //小数位数和单位
                int decimalLen = point.getDecimalLen();
                DecimalFormat df;
                if (decimalLen != 0) {
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < decimalLen; j++) {
                        sb.append("0");
                    }
                    String format = sb.toString();
                    df = new DecimalFormat("0." + format);
                } else {
                    df = new DecimalFormat("0");
                }
                //时间精度为分钟
                Date date = new Date();
                date = new Date(date.getTime() / (60 * 1000) * (60 * 1000));
                String res = df.format(temp);
                point.setValue(res);
                point.setUpdateTime(date);
                point.setId(null);
                points.set(i, point);
                res += point.getUnit();
                executorService.execute(() -> mongoTemplate.insert(point));
                log.info("result: " + res);
                reader++;
            }
            synchronized (ConstantState.ctxRecord) {
                ConstantState.ctxRecord.get(ctx).get(key).setReader(reader);
            }
            new Thread(() -> WebSocketServer.sendMessage()).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

}
