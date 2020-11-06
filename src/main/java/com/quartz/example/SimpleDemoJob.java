package com.quartz.example;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class SimpleDemoJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String id = (String) context.getJobDetail().getJobDataMap().get("id");
        String description = (String) context.getJobDetail().getJobDataMap().get("description");
        System.out.println(Thread.currentThread().getName() + " Executing job " + id + " " + description);
    }
}
