package com.ruoyi.common.enums;

/**
 * @Author: wtao
 * @Date: 2018-12-25 23:41
 * @Version 1.0
 */
public enum StorageType {
    NEVERSAVE(0, "不存储"), SAVE(1, "存储"), CHANGESAVE(2, "变化时存储");
    public Integer code;
    public String desc;

    StorageType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static StorageType getTypeByCode(Integer code) {
        StorageType defaultType = StorageType.NEVERSAVE;
        for (StorageType ftype : StorageType.values()) {
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
