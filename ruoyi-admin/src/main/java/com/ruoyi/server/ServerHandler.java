package com.ruoyi.server;

import com.ruoyi.server.common.ChannelService;
import com.ruoyi.server.common.Constant2;
import com.ruoyi.server.utils.UtilsRegister;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
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
public class ServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Autowired
    ReceiveService receiveService;

    private int count = 0;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        byte[] req = new byte[msg.readableBytes()];
        msg.readBytes(req);
        String code = new String(req);
        String ip = ctx.channel().remoteAddress().toString();
        System.out.println("data: " + code);
//        if (count == 0) {
//            String s = "usr.cnAT+HEARTDT=71707a6d313233";
//            byte[] src = s.getBytes();
//            byte[] send = new byte[src.length + 1];
//            System.arraycopy(src, 0, send, 0, src.length);
//            send[send.length - 1] = 0x0A;
//            msg = ctx.alloc().buffer(send.length);
//            msg.writeBytes(send);
//            ctx.writeAndFlush(msg);
//            count++;
//        }
        if (!UtilsRegister.isRegistered(code, ip, ctx)) {
            ctx.close();
            return;
        }
        Constant2.ctxHeartTime.put(ctx, new Date());
        if (code.length() != 32)
            receiveService.receive(req, ctx);
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
        String code = Constant2.registeredIp.get(address);
        Constant2.registeredIp.remove(address);
        Constant2.ctxIndex.remove(ctx);
        Constant2.ctxHeartTime.remove(ctx);
        if (Constant2.registeredCode.containsKey(code))
            Constant2.registeredCode.put(code, "0");
    }
}
