package com.ruoyi.system.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * @Author: wtao
 * @Date: 2018-12-30 2:05
 * @Version 1.0
 */
@Document(collection = "dataHistorys")
public class SysCollectionPoint {
    @Id
    private String id;
    @Field("pointId")
    private int pointId;
    @Field("pointName")
    private String pointName;
    @Field("slaveName")
    private String slaveName;
    @Field("devId")
    private int devId;
    private int tempId;
    @Field("slaveId")
    private int slaveId;
    private int equNum;
    private int dataType;
    private String registerAdr;
    private int readType;
    private int valueType;
    private int decimalLen;
    private int registerLen;
    private String unit;
    private String formula;
    private int storageType;
    @Field("value")
    private String value;
    @Field("updateTime")
    private Date updateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPointId() {
        return pointId;
    }

    public void setPointId(int pointId) {
        this.pointId = pointId;
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public String getSlaveName() {
        return slaveName;
    }

    public void setSlaveName(String slaveName) {
        this.slaveName = slaveName;
    }

    public int getDevId() {
        return devId;
    }

    public void setDevId(int devId) {
        this.devId = devId;
    }

    public int getTempId() {
        return tempId;
    }

    public void setTempId(int tempId) {
        this.tempId = tempId;
    }

    public int getSlaveId() {
        return slaveId;
    }

    public void setSlaveId(int slaveId) {
        this.slaveId = slaveId;
    }

    public int getEquNum() {
        return equNum;
    }

    public void setEquNum(int equNum) {
        this.equNum = equNum;
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

    public int getReadType() {
        return readType;
    }

    public void setReadType(int readType) {
        this.readType = readType;
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

    public int getRegisterLen() {
        return registerLen;
    }

    public void setRegisterLen(int registerLen) {
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
