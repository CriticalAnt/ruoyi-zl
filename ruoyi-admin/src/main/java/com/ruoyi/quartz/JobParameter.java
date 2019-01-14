package com.ruoyi.quartz;

import java.util.Date;

/**
 * @Author: wtao
 * @Date: 2019-01-08 14:52
 * @Version 1.0
 */
public class JobParameter {
    public static final String JOB_PARAM = "jobParam";
    private String jobName;
    private String jobGroup;
    private String jobTrigger;
    private String status;
    private String cronExpression;
    private Boolean isSync = false;
    private String description;
    private Date updatedTime = new Date();

    public static String getJobParam() {
        return JOB_PARAM;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getJobTrigger() {
        return jobTrigger;
    }

    public void setJobTrigger(String jobTrigger) {
        this.jobTrigger = jobTrigger;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public Boolean getSync() {
        return isSync;
    }

    public void setSync(Boolean sync) {
        isSync = sync;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }
}
