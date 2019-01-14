package com.ruoyi.server.utils;

/**
 * @Author: wtao
 * @Date: 2018/12/19 21:55
 * @Version 1.0
 */
public class UtilsCRC {
    /**
     * 计算CRC16校验码
     *
     * @param bytes
     * @return
     */
    public static int getCRC(byte[] bytes) {
        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;

        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        return CRC;
    }

    public static boolean isCRC(byte[] req) {
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
