package com.ruoyi.common.enums;

/**
 * @Author: wtao
 * @Date: 2018-12-25 23:46
 * @Version 1.0
 */
public enum ReadType {
    READONLY(0, "只读"), READWRITE(1, "读写"), WRITEONLY(2, "只写");
    public Integer code;
    public String desc;

    ReadType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ReadType getTypeByCode(Integer code) {
        ReadType defaultType = ReadType.READONLY;
        for (ReadType ftype : ReadType.values()) {
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
