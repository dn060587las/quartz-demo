package com.quartz.example.web;

import com.quartz.example.SimpleDemoJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.quartz.CronScheduleBuilder.cronSchedule;

@Controller
public class DemoJobController {

    @Autowired
    private SchedulerFactoryBean schedulerBean;

    @GetMapping("/schedulle/{jobId}/{description}")
    @ResponseBody
    public String scheduleJob(@PathVariable String jobId,
                              @PathVariable String description,
                              String cron
    ) throws SchedulerException {
        JobDetail job = newJob(jobId, description);
        schedulerBean.getScheduler().scheduleJob(job, trigger(job, cron));
        return "OK";
    }

    @GetMapping("/delete/{jobId}")
    @ResponseBody
    public String scheduleJob(@PathVariable String jobId) throws SchedulerException {
        schedulerBean.getScheduler().deleteJob(JobKey.jobKey(jobId));
        return "OK";
    }

    private JobDetail newJob(String identity, String description) {
        JobDataMap dataMap = new JobDataMap();
        dataMap.put("id", identity);
        dataMap.put("description", description);
        return JobBuilder.newJob(SimpleDemoJob.class)
                .withIdentity(identity)
                .withDescription(description)
                .setJobData(dataMap)
                .build();
    }

    private CronTrigger trigger(JobDetail jobDetail, String cron) {
        return TriggerBuilder.newTrigger()
                .withIdentity(jobDetail.getKey().getName() + "Trigger", jobDetail.getKey().getGroup())
                .withSchedule(cronSchedule(cron))
                .forJob(jobDetail)
                .build();
    }
}
