package com.ruoyi.server.task;

import com.ruoyi.server.common.Constant2;
import com.ruoyi.server.utils.UtilsCRC;
import com.ruoyi.system.domain.SysCollectionPoint;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: wtao
 * @Date: 2018/12/13 20:21
 * @Version 1.0
 */
@Component
public class SendToClients {

    private Map<String, ChannelHandlerContext> mapChannel = new HashMap<>();

    @Scheduled(cron = "0 0/1 * * * *")
    public void run() {
        try {
            System.out.println(new Date());
            mapChannel.clear();
            synchronized (Constant2.registeredCtx) {
                for (Map.Entry<String, ChannelHandlerContext> entry : Constant2.registeredCtx.entrySet()) {
                    mapChannel.put(entry.getKey(), entry.getValue());
                }
            }
            for (Map.Entry<String, ChannelHandlerContext> entry : mapChannel.entrySet()) {
                List<SysCollectionPoint> points;
                synchronized (Constant2.codePoint) {
                    points = Constant2.codePoint.get(entry.getKey());
                }
                ChannelHandlerContext ctx = entry.getValue();
                for (int i = 0; i < points.size(); i++) {
                    try {
                        SysCollectionPoint point = points.get(i);
                        synchronized (Constant2.ctxIndex) {
                            Constant2.ctxIndex.put(ctx, i);
                        }
                        byte[] bytes = new byte[8];
                        byte[] b = new byte[6];
                        int addr = Integer.valueOf(point.getRegisterAdr().trim()) % 10000 - 1;
                        int code = Integer.valueOf(point.getRegisterAdr()) / 1000;
                        code = code == 3 ? 4 : 3;
                        int len;
                        if (point.getValueType() == 0 || point.getValueType() == 1) {
                            len = 2 / 2;
                        } else {
                            len = 4 / 2;
                        }
                        b[0] = (byte) point.getEquNum();
                        b[1] = (byte) code;
                        b[2] = (byte) ((addr >> 8) & 0xFF);
                        b[3] = (byte) (addr & 0xFF);
                        b[4] = (byte) ((len >> 8) & 0xFF);
                        b[5] = (byte) (len & 0xFF);
                        int crc = UtilsCRC.getCRC(b);
                        bytes[6] = (byte) (crc & 0xFF);
                        bytes[7] = (byte) ((crc >> 8) & 0xFF);
                        System.arraycopy(b, 0, bytes, 0, 6);
                        ByteBuf buf = ctx.alloc().buffer(b.length);
                        buf.writeBytes(bytes);
                        ctx.writeAndFlush(buf);
                        System.out.print("send: ");
                        for (byte a : bytes) {
                            System.out.print(String.valueOf(a & 0xFF) + " ");
                        }
                        System.out.println();
                        Thread.sleep(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
