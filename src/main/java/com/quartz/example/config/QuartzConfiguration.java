package com.quartz.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class QuartzConfiguration {


    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private DataSource dataSource;

    @Bean
    public SchedulerFactoryBean createSchedulerBean() {
        return constructSchedulerFactoryBean(dataSource, transactionManager,  "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate");
    }

    @Bean
    public ExecutorService quartzExecutorService() {
        return Executors.newFixedThreadPool(30);
    }

    private SchedulerFactoryBean constructSchedulerFactoryBean(DataSource dataSource,
                                                               PlatformTransactionManager transactionManager,
                                                               String driverDelegateClass) {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();

        schedulerFactoryBean.setDataSource(dataSource);
        schedulerFactoryBean.setTransactionManager(transactionManager);

        schedulerFactoryBean.setAutoStartup(true);
        schedulerFactoryBean.setSchedulerName("QuartzDemoJobScheduler");

        //Important to use executor with limited threads - if there will be big number of tasks at same time
        schedulerFactoryBean.setTaskExecutor(quartzExecutorService());

        Properties quartzProperties = new Properties();
        quartzProperties.setProperty("org.quartz.scheduler.instanceId", "TEST");

        quartzProperties.setProperty("org.quartz.jobStore.driverDelegateClass", driverDelegateClass);
        quartzProperties.setProperty("org.quartz.jobStore.isClustered", "true");
        quartzProperties.setProperty("org.quartz.jobStore.tablePrefix", "quartz.qrtz_");
        schedulerFactoryBean.setQuartzProperties(quartzProperties);


        return schedulerFactoryBean;
    }

}
