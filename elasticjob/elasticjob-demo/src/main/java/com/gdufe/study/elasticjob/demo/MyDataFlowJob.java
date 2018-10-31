package com.gdufe.study.elasticjob.demo;

import java.util.List;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;

/**
 * @author: laichengfeng
 * @description: 处理数据流工作
 * @Date: 2018/10/17 14:58
 */
public class MyDataFlowJob implements DataflowJob {

    /**
     * 用于抓取
     * 返回为null或长度为空时, 作业停止
     * @param shardingContext
     * @return
     */
    @Override
    public List fetchData(ShardingContext shardingContext) {
        return null;
    }

    /**
     * 用于处理
     * @param shardingContext
     * @param list
     */
    @Override
    public void processData(ShardingContext shardingContext, List list) {

    }
}