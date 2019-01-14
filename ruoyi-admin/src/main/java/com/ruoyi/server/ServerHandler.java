package com.ruoyi.server;

import com.ruoyi.quartz.QuartzManager;
import com.ruoyi.server.common.ChannelService;
import com.ruoyi.server.common.ConstantState;
import com.ruoyi.server.utils.UtilsRegister;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Author: wtao
 * @Date: 2018/12/13 17:47
 * @Version 1.0
 */

@Component
@Qualifier("serverHandler")
@ChannelHandler.Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    ReceiveService receiveService;

    @Autowired
    QuartzManager quartzManager;

    private int count = 0;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        String reg = "usr.cnAT+REGDT=6265383063356433656337363464653238343564623465363963333030363636";
//        "usr.cnAT+REGDT=61323369736b736f3232333431323331343131333431"
//        String reg = "usr.cnAT+REGDT?";
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String code = new String(req);
        String ip = ctx.channel().remoteAddress().toString();
        System.out.println(new Date() + "   data: " + bytesToHex(req) + " : ");
        System.out.println(code);
        for (byte b : req) {
            System.out.print((b & 0xFF) + " ");
        }
        System.out.println();
//        int len = reg.getBytes().length;
//        byte[] send = new byte[len + 1];
//        System.arraycopy(reg.getBytes(), 0, send, 0, len);
//        send[len] = 0x0A;
//        ByteBuf buf2 = ctx.alloc().buffer(len + 1);
//        buf2.writeBytes(send);
//        ctx.writeAndFlush(buf2);
//        System.out.println(reg);
        if (!UtilsRegister.isRegistered(code, ip, ctx)) {
            ctx.close();
            return;
        }
        ConstantState.ctxHeartTime.put(ctx, new Date());
        if (code.length() != 32)
            receiveService.receive2(req, ctx);
    }

//    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
//        byte[] req = new byte[msg.readableBytes()];
//        msg.readBytes(req);
//        String code = new String(req);
//        String ip = ctx.channel().remoteAddress().toString();
//        System.out.println(new Date() + "   data: " + code.length());
//        if (!UtilsRegister.isRegistered(code, ip, ctx)) {
//            ctx.close();
//            return;
//        }
//        ConstantState.ctxHeartTime.put(ctx, new Date());
//        if (code.length() > 100)
//            return;
//        if (code.length() != 32)
//            receiveService.receive(req, ctx);
//    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        unRegister(ctx);
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Disconnect Connection : " + ctx.channel().remoteAddress().toString());
        ChannelService.removeChannel(ctx.channel().remoteAddress().toString());
        unRegister(ctx);
        super.channelInactive(ctx);
    }

    private void unRegister(ChannelHandlerContext ctx) {
        String address = ctx.channel().remoteAddress().toString();
        String code = ConstantState.registeredIp.get(address);
        try {
            quartzManager.deleteJob(address, code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ConstantState.registeredIp.remove(address);
        ConstantState.ctxIndex.remove(ctx);
        ConstantState.ctxHeartTime.remove(ctx);
        ConstantState.registeredCtx.remove(code);
        if (ConstantState.registeredCode.containsKey(code))
            ConstantState.registeredCode.put(code, "0");
    }

    public String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex).append(" ");
        }
        return sb.toString().toUpperCase();
    }
}
