package com.ruoyi.server.task;

import com.ruoyi.quartz.QuartzManager;
import com.ruoyi.server.common.ConstantState;
import com.ruoyi.server.domain.ResolveRecord;
import com.ruoyi.server.utils.UtilsCRC;
import com.ruoyi.system.domain.SysCollectionPoint;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @Author: wtao
 * @Date: 2018/12/13 20:21
 * @Version 1.0
 */
@Component
public class SendToClients {

    private static final Logger log = LoggerFactory.getLogger(SendToClients.class);

    @Autowired
    QuartzManager quartzManager;

    public void run(String regCode, ChannelHandlerContext ctx) throws Exception {
        try {
            Map<String, ResolveRecord> points;
            synchronized (ConstantState.codeRecord) {
                points = ConstantState.codeRecord.get(regCode);
            }
            for (Map.Entry<String, ResolveRecord> mapEntry : points.entrySet()) {
                String key = mapEntry.getKey();
                log.info(key);
                int equNum = Integer.valueOf(key.split("-")[0]);
                int code = Integer.valueOf(key.split("-")[1]);
                List<SysCollectionPoint> list = mapEntry.getValue().getPoints();
                Collections.sort(list, Comparator.comparing(SysCollectionPoint::getRegisterAdr));
                int minAdr = Integer.valueOf(list.get(0).getRegisterAdr()) % 10000 - 1;
                int maxAdr = Integer.valueOf(list.get(list.size() - 1).getRegisterAdr()) % 10000 - 1;
                int lastValueType = list.get(list.size() - 1).getValueType();
                int lastValueLen = lastValueType == 0 ? 1 : lastValueType == 1 ? 1 : 2;
                int len = maxAdr - minAdr + lastValueLen;
                int adr = minAdr;
                while (len > 0) {
                    int qryLen = len > 125 ? 125 : len;
                    byte[] b = new byte[6];
                    byte[] bytes = new byte[8];
                    b[0] = (byte) equNum;
                    b[1] = (byte) code;
                    b[2] = (byte) ((adr >> 8) & 0xFF);
                    b[3] = (byte) (adr & 0xFF);
                    b[4] = (byte) ((qryLen >> 8) & 0xFF);
                    b[5] = (byte) (qryLen & 0xFF);
                    int crc = UtilsCRC.getCRC(b);
                    bytes[6] = (byte) (crc & 0xFF);
                    bytes[7] = (byte) ((crc >> 8) & 0xFF);
                    System.arraycopy(b, 0, bytes, 0, 6);
                    ByteBuf buf = ctx.alloc().buffer(b.length);
                    buf.writeBytes(bytes);
                    ctx.writeAndFlush(buf);
                    log.info("send: " + ctx.channel().remoteAddress().toString());
                    StringBuilder sb = new StringBuilder();
                    for (byte a : bytes) {
                        sb.append(String.valueOf(a & 0xFF) + " ");
                    }
                    log.info(sb.toString());
                    System.out.println();
                    adr += qryLen;
                    len -= 125;
                    Thread.sleep(1000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
