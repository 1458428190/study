package com.gdufe.study.elasticjob.demo.start;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.gdufe.study.elasticjob.demo.MySimpleJob;

/**
 * @author: laichengfeng
 * @description:
 * @Date: 2018/10/17 15:04
 */
public class JobStart {
    public static void main(String[] args) {
        // 启动作业
        new JobScheduler(createRegistryCenter(), createJobConfiguration()).init();
    }

    // 注册中心
    public static CoordinatorRegistryCenter createRegistryCenter() {
        CoordinatorRegistryCenter regCenter = new ZookeeperRegistryCenter(
                new ZookeeperConfiguration("zk_host:2181", "elastic-job-demo"));
        regCenter.init();
        return regCenter;
    }

    public static LiteJobConfiguration createJobConfiguration() {
        // 定义作业核心配置
        JobCoreConfiguration simpleCoreConfig = JobCoreConfiguration
                .newBuilder("demoSimpleJob", "0/15 * * * * ?", 10).build();
        // 定义simple类型配置
        SimpleJobConfiguration simpleJobConfig =
                new SimpleJobConfiguration(simpleCoreConfig, MySimpleJob.class.getCanonicalName());
        // 定义lite作业根配置
        return LiteJobConfiguration.newBuilder(simpleJobConfig).build();
    }
}