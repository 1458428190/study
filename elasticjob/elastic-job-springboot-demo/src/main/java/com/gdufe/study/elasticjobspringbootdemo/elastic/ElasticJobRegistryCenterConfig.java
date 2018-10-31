package com.gdufe.study.elasticjobspringbootdemo.elastic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;

@Configuration
public class ElasticJobRegistryCenterConfig {

    @Value("${collector.qiutan.ej.zk.servers}")
    private String serverLists;

    @Value("${collector.qiutan.ej.zk.namespace}")
    private String namespace;

    @Value("${collector.qiutan.ej.zk.baseSleepTimeMilliseconds}")
    private int baseSleepTimeMilliseconds;

    @Value("${collector.qiutan.ej.zk.maxSleepTimeMilliseconds}")
    private int maxSleepTimeMilliseconds;

    @Value("${collector.qiutan.ej.zk.maxRetries}")
    private int maxRetries;

    @Bean(initMethod = "init", destroyMethod = "close")
    public ZookeeperRegistryCenter regCenter() {
        ZookeeperConfiguration zkConfig = new ZookeeperConfiguration(serverLists, namespace);
        zkConfig.setBaseSleepTimeMilliseconds(baseSleepTimeMilliseconds);
        zkConfig.setMaxSleepTimeMilliseconds(maxSleepTimeMilliseconds);
        zkConfig.setMaxRetries(maxRetries);
        //sessionTimeoutMilliseconds 60000 default
        //connectionTimeoutMilliseconds 15000 default
        return new ZookeeperRegistryCenter(zkConfig);
    }
}
