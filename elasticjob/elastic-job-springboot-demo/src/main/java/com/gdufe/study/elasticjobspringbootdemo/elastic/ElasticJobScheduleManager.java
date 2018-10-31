package com.gdufe.study.elasticjobspringbootdemo.elastic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.gdufe.study.elasticjobspringbootdemo.elastic.job.QTSimpleJob;

/**
 * 分布式定时任务调度管理
 *
 */
@Configuration
//@Profile({ "test", "test2", "release" })
public class ElasticJobScheduleManager {

    @Autowired
    private ElasticJobScheduleFactory jobScheduleFactory;

    @Autowired
    private QTSimpleJob qtSimpleJob;

    /**
     * 5秒一次
     */
    @Bean(initMethod = "init")
    public JobScheduler qtSimpleJobScheduler() {
        return jobScheduleFactory.createSingleShardingJobSchedule(qtSimpleJob, "0/5 *  * * * ?");
    }
}
