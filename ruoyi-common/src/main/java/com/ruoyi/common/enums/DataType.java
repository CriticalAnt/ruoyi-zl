package com.ruoyi.common.enums;

/**
 * @Author: wtao
 * @Date: 2018-12-25 23:27
 * @Version 1.0
 */
public enum DataType {
    VALUE(0, "数值型");
    public Integer code;
    public String desc;

    DataType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static DataType getTypeByCode(Integer code) {
        DataType defaultType = DataType.VALUE;
        for (DataType ftype : DataType.values()) {
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
