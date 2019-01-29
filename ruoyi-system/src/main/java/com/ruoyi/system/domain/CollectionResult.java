package com.ruoyi.system.domain;

/**
 * @Author: wtao
 * @Date: 2019-01-29 14:23
 * @Version 1.0
 */
public class CollectionResult {
    private int devId;
    private int slaveId;
    private int equNum;
    private int pointId;
    private String value;

    public int getPointId() {
        return pointId;
    }

    public void setPointId(int pointId) {
        this.pointId = pointId;
    }

    public int getDevId() {
        return devId;
    }

    public void setDevId(int devId) {
        this.devId = devId;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
