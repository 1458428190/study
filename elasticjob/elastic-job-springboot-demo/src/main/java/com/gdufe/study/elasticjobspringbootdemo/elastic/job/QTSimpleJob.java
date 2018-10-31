package com.gdufe.study.elasticjobspringbootdemo.elastic.job;

import org.springframework.stereotype.Component;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;

@Component
public class QTSimpleJob implements SimpleJob{

    @Override
    public void execute(ShardingContext shardingContext) {

        System.out.println("hello:" + Thread.currentThread().getName());
    }

}
