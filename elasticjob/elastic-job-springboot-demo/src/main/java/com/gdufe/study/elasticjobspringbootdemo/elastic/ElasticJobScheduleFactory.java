package com.gdufe.study.elasticjobspringbootdemo.elastic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.api.strategy.impl.AverageAllocationJobShardingStrategy;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;

/**
 * 分布式作业调度工厂
 */
@Component
public class ElasticJobScheduleFactory {

    @Autowired
    private ZookeeperRegistryCenter regCenter;

    //分片策略
    private final String shardingStrategyClassName = AverageAllocationJobShardingStrategy.class.getCanonicalName();

    /**
     * 创建默认单分片分布式式定时任务
     * 
     * @param simpleJob
     * @param cron
     *            定时配置
     */
    public JobScheduler createSingleShardingJobSchedule(SimpleJob simpleJob, String cron) {
        return new SpringJobScheduler(simpleJob, regCenter, getLiteJobConfiguration(simpleJob.getClass(), cron, 1, ""));
    }

    /**
     * LiteJobConfiguration
     * 
     * @param jobClass
     *            定时任务类
     * @param cron
     *            定时配置
     * @param shardingTotalCount
     *            分片数量
     * @param shardingItemParameters
     *            分片对应参数
     * @return
     */
    private LiteJobConfiguration getLiteJobConfiguration(final Class<? extends SimpleJob> jobClass, final String cron,
        final int shardingTotalCount, final String shardingItemParameters) {
        return LiteJobConfiguration
            .newBuilder(new SimpleJobConfiguration(
                JobCoreConfiguration.newBuilder(jobClass.getName(), cron, shardingTotalCount)
                    .shardingItemParameters(shardingItemParameters).misfire(false).build(),
                jobClass.getCanonicalName()))
            .overwrite(true).jobShardingStrategyClass(shardingStrategyClassName).build();
    }
}
