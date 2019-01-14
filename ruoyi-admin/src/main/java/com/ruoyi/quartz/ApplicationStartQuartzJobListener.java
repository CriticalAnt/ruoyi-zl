//package com.ruoyi.quartz;
//
//import org.quartz.Scheduler;
//import org.quartz.SchedulerException;
//import org.quartz.SchedulerFactory;
//import org.quartz.impl.StdSchedulerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationListener;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.event.ContextRefreshedEvent;
//
///**
// * @Author: wtao
// * @Date: 2019-01-08 15:03
// * @Version 1.0
// */
////@Configuration
//public class ApplicationStartQuartzJobListener implements ApplicationListener<ContextRefreshedEvent> {
//
//    @Autowired
//    private QuartzScheduler quartzScheduler;
//
//    @Override
//    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
//        try {
//            quartzScheduler.startJob();
//        } catch (SchedulerException e) {
//            e.printStackTrace();
//        }
//        System.out.println("任务已经启动...");
//    }
//
//    @Bean
//    public Scheduler scheduler() throws SchedulerException {
//        SchedulerFactory schedulerFactoryBean = new StdSchedulerFactory();
//        return schedulerFactoryBean.getScheduler();
//    }
//}
