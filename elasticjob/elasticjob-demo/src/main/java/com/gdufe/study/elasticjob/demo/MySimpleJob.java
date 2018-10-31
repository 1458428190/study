/**
 * @(#)MySimpleJob.java, 2018/10/17.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.gdufe.study.elasticjob.demo;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;

/**
 * @author: laichengfeng (laichengfeng @ corp.netease.com)
 * @description: 简单类型工作
 * @Date: 2018/10/17 14:58
 */
public class MySimpleJob implements SimpleJob {

    @Override
    public void execute(ShardingContext shardingContext) {

    }
}