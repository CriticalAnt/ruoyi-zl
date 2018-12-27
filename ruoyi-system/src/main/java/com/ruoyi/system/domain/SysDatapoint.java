package com.ruoyi.system.domain;

import com.ruoyi.common.base.BaseEntity;

/**
 * @Author: wtao
 * @Date: 2018-12-25 17:18
 * @Version 1.0
 */
public class SysDatapoint extends BaseEntity {

    private int id;
    private int tempId;
    private String pointName;
    private int dataType;
    private String registerAdr;
    private int valueType;
    private int decimalLen;
    private int readType;
    private String registerLen;
    private String unit;
    private String formula;
    private int storageType;
    private int delFlag;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTempId() {
        return tempId;
    }

    public void setTempId(int tempId) {
        this.tempId = tempId;
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public String getRegisterAdr() {
        return registerAdr;
    }

    public void setRegisterAdr(String registerAdr) {
        this.registerAdr = registerAdr;
    }

    public int getValueType() {
        return valueType;
    }

    public void setValueType(int valueType) {
        this.valueType = valueType;
    }

    public int getDecimalLen() {
        return decimalLen;
    }

    public void setDecimalLen(int decimalLen) {
        this.decimalLen = decimalLen;
    }

    public int getReadType() {
        return readType;
    }

    public void setReadType(int readType) {
        this.readType = readType;
    }

    public String getRegisterLen() {
        return registerLen;
    }

    public void setRegisterLen(String registerLen) {
        this.registerLen = registerLen;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public int getStorageType() {
        return storageType;
    }

    public void setStorageType(int storageType) {
        this.storageType = storageType;
    }

    public int getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(int delFlag) {
        this.delFlag = delFlag;
    }
}
