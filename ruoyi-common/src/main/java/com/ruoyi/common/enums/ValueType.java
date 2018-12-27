package com.ruoyi.common.enums;

/**
 * @Author: wtao
 * @Date: 2018-12-25 23:33
 * @Version 1.0
 */
public enum ValueType {
    TWOBYTESUNSIGN(0, "2字节无符号整数"), TWOBYTESSIGN(1, "2字节有符号整数"),
    FOURBYTESUNSIGN(2, "4字节无符号整数(AB CD)"), FOURBYTESUNSIGNBIG(3, "4字节无符号整数(CD AB)"),
    FOURBYTESSIGN(4, "4字节有符号整数(AB CD)"), FOURBYTESSIGNBIG(5, "4字节无符号整数(CD AB)"),
    FOURBYTESDOUBLE(6, "4字节浮点型(AB CD)"), FOURBYTESDOUBLEBIG(7, "4字节浮点型(CD AB)");

    public Integer code;
    public String desc;

    ValueType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ValueType getTypeByCode(Integer code) {
        ValueType defaultType = ValueType.TWOBYTESUNSIGN;
        for (ValueType ftype : ValueType.values()) {
            if (ftype.code == code) {
                return ftype;
            }
        }
        return defaultType;
    }

    public static String getDescByCode(Integer code) {
        return getTypeByCode(code).desc;
    }
}
