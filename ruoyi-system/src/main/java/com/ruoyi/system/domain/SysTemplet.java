package com.ruoyi.system.domain;

import com.ruoyi.common.base.BaseEntity;

/**
 * @Author: wtao
 * @Date: 2018-12-23 17:58
 * @Version 1.0
 */
public class SysTemplet extends BaseEntity {

    private int id;
    private String modelName;
    private int delFlag;
    private int relationCount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public int getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(int delFlag) {
        this.delFlag = delFlag;
    }

    public int getRelationCount() {
        return relationCount;
    }

    public void setRelationCount(int relationCount) {
        this.relationCount = relationCount;
    }
}
