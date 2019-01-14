package com.ruoyi.server.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: wtao
 * @Date: 2019-01-06 2:43
 * @Version 1.0
 * @deprecated 自定义解码器, 处理断包粘包, 防止socket攻击
 */

/**
 * 请求解码器
 * <pre>
 * 数据包格式
 * +——----——+——-----——+——----——+——----——+——-----——+
 * | 从机号  | 功能码   | 长度    |  数据   | CRC校验  |
 * +——----——+——-----——+——----——+——----——+——-----——+
 * </pre>
 * 从机号1字节
 * 功能码1字节
 * 长度1字节
 * 数据len字节
 * CRC校验2字节
 */
public class CustomDecoder extends ByteToMessageDecoder {

    public static final int BASE_LENGTH = 1 + 1 + 1;
    public static final int REGISTER_LEN = 32;
    public static final byte[] HEART_BEATS = new byte[]{0x71, 0x70, 0x7A, 0x6D, 0x31, 0x32, 0x33};
    public static final String HEART = "qpzm123";

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf bufferIn, List<Object> out) throws Exception {

        //记录读指针位置
        int beginReader = bufferIn.readerIndex();
        //注册码、心跳包处理
        if (bufferIn.readableBytes() == REGISTER_LEN) {
            bufferIn.readerIndex(REGISTER_LEN);
            ByteBuf otherByteBufRef = bufferIn.slice(beginReader, REGISTER_LEN);
            otherByteBufRef.retain();
            out.add(otherByteBufRef);
        } else if (bufferIn.readableBytes() == 7) {
            bufferIn.readerIndex(7);
            //验证是否心跳包
            byte[] req = new byte[7];
            //读取指定长度数据
            ByteBuf otherByteBufRef = bufferIn.slice(beginReader, 7);
            int reader = otherByteBufRef.readerIndex();
            otherByteBufRef.readBytes(req);
            if (Arrays.equals(req, HEART_BEATS)) {
                //重置reader,原因是上面做判断读取了otherByteBufRef指针
                otherByteBufRef.readerIndex(reader);
                otherByteBufRef.retain();
                out.add(otherByteBufRef);
            } else {
                bufferIn.readerIndex(beginReader);
                return;
            }
        }

        //长度不足3字节 等待
        if (bufferIn.readableBytes() < BASE_LENGTH) {
            return;
        }
        //长度超过2048字节,认为是非法数据,跳过
        if (bufferIn.readableBytes() > 2048) {
            bufferIn.skipBytes(bufferIn.readableBytes());
        }
        byte[] temp = new byte[3];
        //读从机号
        temp[0] = bufferIn.readByte();
        //读功能码
        temp[1] = bufferIn.readByte();
        //读长度
        temp[2] = bufferIn.readByte();
        String heart = new String(temp);
        if (HEART.startsWith(heart) && bufferIn.readableBytes() >= 4) {
            bufferIn.readerIndex(beginReader);
            bufferIn.readerIndex(beginReader + 7);
            ByteBuf otherByteBufRef = bufferIn.slice(beginReader, 7);
            otherByteBufRef.retain();
            out.add(otherByteBufRef);
        }
        int len = temp[2] & 0xFF;
        System.out.println(len + "    " + bufferIn.readableBytes());
        //未满足长度,重置读指针,继续等待
        if (len + 2 > bufferIn.readableBytes()) {
            bufferIn.readerIndex(beginReader);
            return;
        }
        bufferIn.readerIndex(beginReader);
        //满足长度要求,设置读指针在下一个数据包包头
        bufferIn.readerIndex(beginReader + 5 + len);
        //读取指定长度数据
        ByteBuf otherByteBufRef = bufferIn.slice(beginReader, 5 + len);
        otherByteBufRef.retain();
        out.add(otherByteBufRef);
    }
}
