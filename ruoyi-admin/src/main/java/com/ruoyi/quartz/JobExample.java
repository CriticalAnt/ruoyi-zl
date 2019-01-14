package com.ruoyi.quartz;

import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @Author: wtao
 * @Date: 2019-01-08 14:51
 * @Version 1.0
 */
public class JobExample {

    @Autowired
    Scheduler scheduler;

    public void run() throws Exception {
        // Job的配置信息，可以从数据库或配置文件中获取
        List<JobParameter> list = new ArrayList<>();
        String jobGroup = "JobGroup1";
        for (int i = 1; i < 4; i++) {
            JobParameter param = new JobParameter();
            param.setJobName("Job" + i);
            param.setJobGroup(jobGroup);
            param.setCronExpression("0/5 * * * * ?");
            param.setDescription("Execute job " + i + " every 5 seconds ...");
            list.add(param);
        }
        try {
            // ### 创建并启动job ###
            for (JobParameter param : list) {
                // 构建job信息
                JobDetail jobDetail = JobBuilder.newJob(SchedulerQuartzJob.class)
                        .withIdentity(param.getJobName(), param.getJobGroup()).build();
                // 表达式调度构建器
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(param.getCronExpression());
                // 按cronExpression表达式构建trigger
                CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(param.getJobName(), param.getJobGroup())
                        .withSchedule(scheduleBuilder).build();
                // 放入参数，运行时的方法可以获取
                jobDetail.getJobDataMap().put("jobParam", param);
                scheduler.scheduleJob(jobDetail, trigger);
            }
            // ### 移除job3 ###
            Thread.sleep(5000);
            System.out.println("### 移除job3 ###");
            scheduler.deleteJob(JobKey.jobKey("Job3", jobGroup));
            // ### 暂停job1和job2 ###
            Thread.sleep(5000);
            System.out.println("### 暂停job1和job2 ###");
            scheduler.pauseJob(JobKey.jobKey("Job1", jobGroup));
            scheduler.pauseJob(JobKey.jobKey("Job2", jobGroup));
            // ### 再次启动job1 ###
            Thread.sleep(5000);
            System.out.println("### 再次启动job1 ###");
            scheduler.resumeJob(JobKey.jobKey("Job1", jobGroup));
            // ### 修改job1的cron ###
            Thread.sleep(5000);
            System.out.println("### 修改job1的cron为每3秒执行一次 ###");
            TriggerKey triggerKey = TriggerKey.triggerKey("Job1", jobGroup);
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0/3 * * * * ?");
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
            // 获取job参数并修改描述
            JobParameter jobParam = (JobParameter) scheduler.getJobDetail(JobKey.jobKey("Job1", jobGroup))
                    .getJobDataMap().get(JobParameter.JOB_PARAM);
            jobParam.setCronExpression("0/3 * * * * ?");
            jobParam.setDescription("Execute job 1 every 3 seconds ...");
            scheduler.rescheduleJob(triggerKey, trigger);
            // 打印内存中的所有 Job
            Thread.sleep(5000);
            System.out.println("### 打印内存中的所有 Job的状态 ###");
            Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(GroupMatcher.anyTriggerGroup());
            for (TriggerKey tKey : triggerKeys) {
                CronTrigger t = (CronTrigger) scheduler.getTrigger(tKey);
                System.out.println("Trigger details: " + t.getJobKey().getName() + ", " + t.getJobKey().getGroup()
                        + ", " + scheduler.getTriggerState(tKey) + ", " + t.getFinalFireTime() + ", "
                        + t.getCronExpression());
            }
        } finally {
            System.out.println("Spring context is closed.");
        }
    }
}
