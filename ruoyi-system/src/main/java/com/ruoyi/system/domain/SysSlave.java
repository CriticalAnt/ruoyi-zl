package com.ruoyi.system.domain;

import com.ruoyi.common.base.BaseEntity;

/**
 * @Author: wtao
 * @Date: 2018-12-28 2:40
 * @Version 1.0
 */
public class SysSlave extends BaseEntity {

    private int id;
    private int devId;
    private int slaveNum;
    private String slaveName;
    private int tempId;
    private int delFlag;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDevId() {
        return devId;
    }

    public void setDevId(int devId) {
        this.devId = devId;
    }

    public int getSlaveNum() {
        return slaveNum;
    }

    public void setSlaveNum(int slaveNum) {
        this.slaveNum = slaveNum;
    }

    public String getSlaveName() {
        return slaveName;
    }

    public void setSlaveName(String slaveName) {
        this.slaveName = slaveName;
    }

    public int getTempId() {
        return tempId;
    }

    public void setTempId(int tempId) {
        this.tempId = tempId;
    }

    public int getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(int delFlag) {
        this.delFlag = delFlag;
    }
}
