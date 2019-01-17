package com.ruoyi.quartz;

import com.ruoyi.server.task.SendToClients;
import io.netty.channel.ChannelHandlerContext;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: wtao
 * @Date: 2019-01-08 14:44
 * @Version 1.0
 */
public class SchedulerQuartzJob implements Job {

    @Autowired
    SendToClients sendToClients;

    @Override
    public void execute(JobExecutionContext arg0) {
        JobDataMap jobDataMap = arg0.getJobDetail().getJobDataMap();
        String key = jobDataMap.getKeys()[0];
        ChannelHandlerContext ctx = (ChannelHandlerContext) jobDataMap.get(key);
        String code = arg0.getTrigger().getJobKey().getGroup();
        try {
            sendToClients.run(code, ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}